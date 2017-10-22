package com.gianlu.aria2app.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gianlu.aria2app.NetIO.JTA2.Peer;
import com.gianlu.aria2app.R;
import com.gianlu.commonutils.CommonUtils;
import com.gianlu.commonutils.Sorting.NotFilterable;
import com.gianlu.commonutils.Sorting.OrderedRecyclerViewAdapter;
import com.gianlu.commonutils.SuperTextView;

import java.util.Comparator;
import java.util.List;

public class PeersAdapter extends OrderedRecyclerViewAdapter<PeersAdapter.ViewHolder, Peer, PeersAdapter.SortBy, NotFilterable> {
    private final IAdapter handler;
    private final LayoutInflater inflater;

    public PeersAdapter(Context context, List<Peer> peers, IAdapter handler) {
        super(peers, SortBy.DOWNLOAD_SPEED);
        this.inflater = LayoutInflater.from(context);
        this.handler = handler;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    @Override
    protected void onBindViewHolder(ViewHolder holder, int position, @NonNull Peer payload) {
        holder.downloadSpeed.setText(CommonUtils.speedFormatter(payload.downloadSpeed, false));
        holder.uploadSpeed.setText(CommonUtils.speedFormatter(payload.uploadSpeed, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Peer peer = objs.get(position);
        holder.address.setText(peer.ip + ":" + peer.port);
        holder.downloadSpeed.setText(CommonUtils.speedFormatter(peer.downloadSpeed, false));
        holder.uploadSpeed.setText(CommonUtils.speedFormatter(peer.uploadSpeed, false));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) handler.onPeerSelected(peer);
            }
        });
    }

    @Nullable
    @Override
    protected RecyclerView getRecyclerView() {
        if (handler != null) return handler.getRecyclerView();
        else return null;
    }

    @Override
    protected boolean matchQuery(Peer item, String query) {
        return true;
    }

    @Override
    protected void shouldUpdateItemCount(int count) {
        if (handler != null) handler.onItemCountUpdated(count);
    }

    @NonNull
    @Override
    public Comparator<Peer> getComparatorFor(SortBy sorting) {
        switch (sorting) {
            default:
            case DOWNLOAD_SPEED:
                return new Peer.DownloadSpeedComparator();
            case UPLOAD_SPEED:
                return new Peer.UploadSpeedComparator();
        }
    }

    public enum SortBy {
        DOWNLOAD_SPEED,
        UPLOAD_SPEED
    }

    public interface IAdapter {
        void onPeerSelected(Peer peer);

        void onItemCountUpdated(int count);

        @Nullable
        RecyclerView getRecyclerView();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final SuperTextView address;
        final SuperTextView downloadSpeed;
        final SuperTextView uploadSpeed;

        ViewHolder(ViewGroup parent) {
            super(inflater.inflate(R.layout.peer_item, parent, false));
            address = itemView.findViewById(R.id.peerItem_address);
            downloadSpeed = itemView.findViewById(R.id.peerItem_downloadSpeed);
            uploadSpeed = itemView.findViewById(R.id.peerItem_uploadSpeed);
        }
    }
}
