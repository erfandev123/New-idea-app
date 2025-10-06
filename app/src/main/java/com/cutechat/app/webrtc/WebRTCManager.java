package com.cutechat.app.webrtc;

import android.content.Context;
import android.util.Log;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RTCStatsReport;
import org.webrtc.RtpReceiver;
import org.webrtc.RtpSender;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class WebRTCManager implements PeerConnection.Observer, SdpObserver {

    private static final String TAG = "WebRTCManager";
    private static WebRTCManager instance;

    private Context context;
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private EglBase eglBase;
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private AudioSource audioSource;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private MediaStream localStream;
    private CallListener callListener;
    private boolean isVideoCall;
    private String remoteUserId;

    public interface CallListener {
        void onCallConnected();
        void onCallEnded();
        void onCallFailed(String error);
    }

    private WebRTCManager(Context context) {
        this.context = context;
        initializePeerConnectionFactory();
    }

    public static synchronized WebRTCManager getInstance(Context context) {
        if (instance == null) {
            instance = new WebRTCManager(context);
        }
        return instance;
    }

    private void initializePeerConnectionFactory() {
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .setEnableInternalTracer(true)
                        .setFieldTrials("")
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .createPeerConnectionFactory();

        eglBase = EglBase.create();
    }

    public EglBase getEglBase() {
        return eglBase;
    }

    public void setCallListener(CallListener listener) {
        this.callListener = listener;
    }

    public void startCall(String userId, boolean isVideoCall) {
        this.remoteUserId = userId;
        this.isVideoCall = isVideoCall;
        
        createPeerConnection();
        createLocalStream();
        
        // TODO: Send offer to remote user via Firebase signaling
        createOffer();
    }

    public void answerCall() {
        // TODO: Answer incoming call
        if (callListener != null) {
            callListener.onCallConnected();
        }
    }

    public void declineCall() {
        // TODO: Decline incoming call
        cleanup();
    }

    public void endCall() {
        // TODO: End call and cleanup
        cleanup();
    }

    private void createPeerConnection() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        
        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, this);
    }

    private void createLocalStream() {
        localStream = peerConnectionFactory.createLocalMediaStream("local_stream");

        // Create audio track
        audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        localAudioTrack = peerConnectionFactory.createAudioTrack("audio_track", audioSource);
        localStream.addTrack(localAudioTrack);

        // Create video track if it's a video call
        if (isVideoCall) {
            videoCapturer = createVideoCapturer();
            if (videoCapturer != null) {
                videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
                SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
                videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
                videoCapturer.startCapture(1280, 720, 30);

                localVideoTrack = peerConnectionFactory.createVideoTrack("video_track", videoSource);
                localStream.addTrack(localVideoTrack);
            }
        }

        peerConnection.addStream(localStream);
    }

    private VideoCapturer createVideoCapturer() {
        CameraEnumerator enumerator = new Camera2Enumerator(context);
        String[] deviceNames = enumerator.getDeviceNames();
        
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }
        
        return null;
    }

    private void createOffer() {
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        if (isVideoCall) {
            constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        }
        
        peerConnection.createOffer(this, constraints);
    }

    @Override
    public void onCreateSuccess(SessionDescription sessionDescription) {
        peerConnection.setLocalDescription(this, sessionDescription);
        // TODO: Send offer to remote user via Firebase
    }

    @Override
    public void onSetSuccess() {
        Log.d(TAG, "Set local/remote description success");
    }

    @Override
    public void onCreateFailure(String error) {
        Log.e(TAG, "Create offer/answer failure: " + error);
        if (callListener != null) {
            callListener.onCallFailed(error);
        }
    }

    @Override
    public void onSetFailure(String error) {
        Log.e(TAG, "Set local/remote description failure: " + error);
        if (callListener != null) {
            callListener.onCallFailed(error);
        }
    }

    @Override
    public void onIceCandidate(IceCandidate iceCandidate) {
        // TODO: Send ICE candidate to remote user via Firebase
        Log.d(TAG, "ICE candidate: " + iceCandidate.toString());
    }

    @Override
    public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
        Log.d(TAG, "ICE candidates removed");
    }

    @Override
    public void onSignalingChange(PeerConnection.SignalingState signalingState) {
        Log.d(TAG, "Signaling state: " + signalingState.toString());
    }

    @Override
    public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
        Log.d(TAG, "ICE connection state: " + iceConnectionState.toString());
        if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
            if (callListener != null) {
                callListener.onCallConnected();
            }
        } else if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED ||
                   iceConnectionState == PeerConnection.IceConnectionState.FAILED) {
            if (callListener != null) {
                callListener.onCallEnded();
            }
        }
    }

    @Override
    public void onIceConnectionReceivingChange(boolean receiving) {
        Log.d(TAG, "ICE connection receiving change: " + receiving);
    }

    @Override
    public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
        Log.d(TAG, "ICE gathering state: " + iceGatheringState.toString());
    }

    @Override
    public void onAddStream(MediaStream mediaStream) {
        Log.d(TAG, "Remote stream added");
        // TODO: Handle remote stream
    }

    @Override
    public void onRemoveStream(MediaStream mediaStream) {
        Log.d(TAG, "Remote stream removed");
    }

    @Override
    public void onDataChannel(DataChannel dataChannel) {
        Log.d(TAG, "Data channel received");
    }

    @Override
    public void onRenegotiationNeeded() {
        Log.d(TAG, "Renegotiation needed");
    }

    @Override
    public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
        Log.d(TAG, "Track added");
    }

    public void cleanup() {
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
            } catch (InterruptedException e) {
                Log.e(TAG, "Error stopping video capture", e);
            }
            videoCapturer.dispose();
            videoCapturer = null;
        }

        if (localStream != null) {
            localStream.dispose();
            localStream = null;
        }

        if (peerConnection != null) {
            peerConnection.dispose();
            peerConnection = null;
        }

        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
            peerConnectionFactory = null;
        }

        if (eglBase != null) {
            eglBase.release();
            eglBase = null;
        }
    }
}