package com.cutechat.app.webrtc;

import android.content.Context;
import android.util.Log;

import com.cutechat.app.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WebRTCManager {
    
    private static final String TAG = "WebRTCManager";
    private static final String LOCAL_STREAM_ID = "local_stream";
    private static final String VIDEO_TRACK_ID = "video_track";
    private static final String AUDIO_TRACK_ID = "audio_track";

    private final Context context;
    private final EglBase eglBase;
    private final PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;
    private VideoCapturer videoCapturer;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private SurfaceViewRenderer localVideoView;
    private SurfaceViewRenderer remoteVideoView;
    
    private boolean isAudioMuted = false;
    private boolean isVideoEnabled = true;
    private boolean isFrontCamera = true;

    private WebRTCListener listener;
    private DatabaseReference callRef;

    public interface WebRTCListener {
        void onLocalStreamReady();
        void onRemoteStreamReady();
        void onDisconnected();
        void onError(String error);
    }

    public WebRTCManager(Context context) {
        this.context = context;
        this.eglBase = EglBase.create();
        
        // Initialize PeerConnectionFactory
        PeerConnectionFactory.InitializationOptions initOptions =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .setEnableInternalTracer(true)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initOptions);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        
        DefaultVideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(
                eglBase.getEglBaseContext(), true, true);
        DefaultVideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(
                eglBase.getEglBaseContext());

        this.peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    public void initializeSurfaceViews(SurfaceViewRenderer localView, SurfaceViewRenderer remoteView) {
        this.localVideoView = localView;
        this.remoteVideoView = remoteView;
        
        localVideoView.init(eglBase.getEglBaseContext(), null);
        remoteVideoView.init(eglBase.getEglBaseContext(), null);
        
        localVideoView.setMirror(true);
        localVideoView.setEnableHardwareScaler(true);
        remoteVideoView.setEnableHardwareScaler(true);
    }

    public void startLocalStream(boolean videoEnabled) {
        MediaStream localStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID);

        // Audio
        AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
        localAudioTrack = peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
        localStream.addTrack(localAudioTrack);

        // Video
        if (videoEnabled) {
            videoCapturer = createVideoCapturer();
            if (videoCapturer != null) {
                VideoSource videoSource = peerConnectionFactory.createVideoSource(
                        videoCapturer.isScreencast());
                
                SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create(
                        "CaptureThread", eglBase.getEglBaseContext());
                
                videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
                videoCapturer.startCapture(720, 1280, 30);

                localVideoTrack = peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource);
                localVideoTrack.addSink(localVideoView);
                localStream.addTrack(localVideoTrack);
            }
        }

        if (listener != null) {
            listener.onLocalStreamReady();
        }
    }

    public void createPeerConnection(DatabaseReference callRef) {
        this.callRef = callRef;
        
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        for (String server : Constants.STUN_SERVERS) {
            iceServers.add(PeerConnection.IceServer.builder(server).createIceServer());
        }

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;

        peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, new PeerConnectionObserver());

        // Add local stream
        MediaStream localStream = peerConnectionFactory.createLocalMediaStream(LOCAL_STREAM_ID);
        if (localAudioTrack != null) {
            localStream.addTrack(localAudioTrack);
        }
        if (localVideoTrack != null) {
            localStream.addTrack(localVideoTrack);
        }
        peerConnection.addStream(localStream);
    }

    public void createOffer() {
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

        peerConnection.createOffer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        Map<String, Object> offer = new HashMap<>();
                        offer.put("type", sessionDescription.type.canonicalForm());
                        offer.put("sdp", sessionDescription.description);
                        callRef.child("offer").setValue(offer);
                    }
                }, sessionDescription);
            }
        }, constraints);
    }

    public void createAnswer() {
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

        peerConnection.createAnswer(new SdpObserver() {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                peerConnection.setLocalDescription(new SdpObserver() {
                    @Override
                    public void onSetSuccess() {
                        Map<String, Object> answer = new HashMap<>();
                        answer.put("type", sessionDescription.type.canonicalForm());
                        answer.put("sdp", sessionDescription.description);
                        callRef.child("answer").setValue(answer);
                    }
                }, sessionDescription);
            }
        }, constraints);
    }

    public void onRemoteSessionReceived(SessionDescription sessionDescription) {
        peerConnection.setRemoteDescription(new SdpObserver() {}, sessionDescription);
    }

    public void addIceCandidate(IceCandidate candidate) {
        if (peerConnection != null) {
            peerConnection.addIceCandidate(candidate);
        }
    }

    public void toggleAudio() {
        if (localAudioTrack != null) {
            isAudioMuted = !isAudioMuted;
            localAudioTrack.setEnabled(!isAudioMuted);
        }
    }

    public void toggleVideo() {
        if (localVideoTrack != null) {
            isVideoEnabled = !isVideoEnabled;
            localVideoTrack.setEnabled(isVideoEnabled);
        }
    }

    public void switchCamera() {
        if (videoCapturer instanceof org.webrtc.CameraVideoCapturer) {
            ((org.webrtc.CameraVideoCapturer) videoCapturer).switchCamera(null);
            isFrontCamera = !isFrontCamera;
            if (localVideoView != null) {
                localVideoView.setMirror(isFrontCamera);
            }
        }
    }

    public void close() {
        if (videoCapturer != null) {
            try {
                videoCapturer.stopCapture();
                videoCapturer.dispose();
            } catch (InterruptedException e) {
                Log.e(TAG, "Error stopping camera", e);
            }
        }
        
        if (localVideoTrack != null) {
            localVideoTrack.dispose();
        }
        
        if (localAudioTrack != null) {
            localAudioTrack.dispose();
        }
        
        if (peerConnection != null) {
            peerConnection.close();
            peerConnection = null;
        }
        
        if (localVideoView != null) {
            localVideoView.release();
        }
        
        if (remoteVideoView != null) {
            remoteVideoView.release();
        }
    }

    private VideoCapturer createVideoCapturer() {
        CameraEnumerator enumerator = new Camera2Enumerator(context);
        String[] deviceNames = enumerator.getDeviceNames();

        // Try front camera first
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                VideoCapturer capturer = enumerator.createCapturer(deviceName, null);
                if (capturer != null) {
                    return capturer;
                }
            }
        }

        // Try back camera
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                VideoCapturer capturer = enumerator.createCapturer(deviceName, null);
                if (capturer != null) {
                    isFrontCamera = false;
                    return capturer;
                }
            }
        }

        return null;
    }

    public void setListener(WebRTCListener listener) {
        this.listener = listener;
    }

    public boolean isAudioMuted() {
        return isAudioMuted;
    }

    public boolean isVideoEnabled() {
        return isVideoEnabled;
    }

    // PeerConnection Observer
    private class PeerConnectionObserver implements PeerConnection.Observer {
        @Override
        public void onSignalingChange(PeerConnection.SignalingState signalingState) {
            Log.d(TAG, "onSignalingChange: " + signalingState);
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            Log.d(TAG, "onIceConnectionChange: " + iceConnectionState);
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED ||
                iceConnectionState == PeerConnection.IceConnectionState.FAILED) {
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        }

        @Override
        public void onIceConnectionReceivingChange(boolean b) {
            Log.d(TAG, "onIceConnectionReceivingChange: " + b);
        }

        @Override
        public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
            Log.d(TAG, "onIceGatheringChange: " + iceGatheringState);
        }

        @Override
        public void onIceCandidate(IceCandidate iceCandidate) {
            Log.d(TAG, "onIceCandidate: " + iceCandidate);
            if (callRef != null) {
                Map<String, Object> candidate = new HashMap<>();
                candidate.put("sdpMid", iceCandidate.sdpMid);
                candidate.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
                candidate.put("candidate", iceCandidate.sdp);
                callRef.child("candidates").push().setValue(candidate);
            }
        }

        @Override
        public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
            Log.d(TAG, "onIceCandidatesRemoved");
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            Log.d(TAG, "onAddStream: " + mediaStream.getId());
            if (mediaStream.videoTracks.size() > 0) {
                VideoTrack remoteVideoTrack = mediaStream.videoTracks.get(0);
                remoteVideoTrack.addSink(remoteVideoView);
                if (listener != null) {
                    listener.onRemoteStreamReady();
                }
            }
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {
            Log.d(TAG, "onRemoveStream");
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            Log.d(TAG, "onDataChannel");
        }

        @Override
        public void onRenegotiationNeeded() {
            Log.d(TAG, "onRenegotiationNeeded");
        }

        @Override
        public void onAddTrack(org.webrtc.RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
            Log.d(TAG, "onAddTrack");
        }
    }

    // SDP Observer
    private static class SdpObserver implements org.webrtc.SdpObserver {
        @Override
        public void onCreateSuccess(SessionDescription sessionDescription) {
            Log.d(TAG, "SDP onCreateSuccess");
        }

        @Override
        public void onSetSuccess() {
            Log.d(TAG, "SDP onSetSuccess");
        }

        @Override
        public void onCreateFailure(String s) {
            Log.e(TAG, "SDP onCreateFailure: " + s);
        }

        @Override
        public void onSetFailure(String s) {
            Log.e(TAG, "SDP onSetFailure: " + s);
        }
    }
}
