package io.github.tavisco.rvglassistant.objects.RecyclerViewItems;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.tavisco.rvglassistant.R;
import io.github.tavisco.rvglassistant.objects.Constants;
import io.github.tavisco.rvglassistant.utils.DownloadUtils;

/**
 * Created by Tavisco on 25/05/18.
 */
public class PackageItem extends AbstractItem<PackageItem, PackageItem.ViewHolder> {

    String name;
    String localVersion;
    String lastVersion;
    int downloadID;
    boolean downloadOngoing;
    boolean versionChecked = false;

    public PackageItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLocalVersion() {
        return localVersion;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLocalVersion(String localVersion) {
        this.localVersion = localVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public String getDownloadLink(){
        return Constants.RVIO_DOWNLOAD_PACKS_LINK.concat(getName()).concat(".zip");
    }

    public String getDownloadSavePath(){
        return Constants.PATH_RVGL_BUTLER + File.separator + getName().concat(".zip");
    }

    public int getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(int downloadID) {
        this.downloadID = downloadID;
    }

    public boolean isDownloadOngoing() {
        return downloadOngoing;
    }

    public void setDownloadOngoing(boolean downloadOngoing, @NonNull final PackageItem.ViewHolder viewHolder) {
        this.downloadOngoing = downloadOngoing;
        if (downloadOngoing){
            viewHolder.lytPackageDownload.setVisibility(View.VISIBLE);
        } else {
            viewHolder.lytPackageDownload.setVisibility(View.GONE);
        }
    }

    public void updateDownloadView(Context ctx, @NonNull final PackageItem.ViewHolder vh, long etaInMilliSeconds, long downloadedBytesPerSecond, int progress, String status){
        vh.tvDownloadSpeed.setText(DownloadUtils.getDownloadSpeedString(ctx, downloadedBytesPerSecond));
        vh.tvTimeRemaining.setText(DownloadUtils.getETAString(ctx, etaInMilliSeconds));
        vh.barDownloadProgress.setProgress(progress);
        vh.tvDownloadProgress.setText(String.valueOf(progress).concat("%"));
    }

    /**
     * defines the type defining this item. must be unique. preferably an id
     *
     * @return the type
     */
    @Override
    public int getType() {
        return R.id.rvio_package_id;
    }

    /**
     * defines the layout which will be used for this item in the list
     *
     * @return the layout for this item
     */
    @Override
    public int getLayoutRes() {
        return R.layout.package_item;
    }

    /**
     * binds the data of this item onto the viewHolder
     *
     * @param viewHolder the viewHolder of this item
     */
    @Override
    public void bindView(@NonNull final PackageItem.ViewHolder viewHolder, @NonNull List<Object> payloads) {
        super.bindView(viewHolder, payloads);

        if (!versionChecked){
            viewHolder.tvPackageTitle.setText(getName());
            viewHolder.tvPackageLocalVersion.setText("Local: Checking");
            viewHolder.tvPackageLastVersion.setText("Last: Checking");

            // Ex. https://distribute.re-volt.io/assets/io_cars.txt
            String rvioRequest = Constants.RVIO_ASSETS_LINK + getName() + ".txt";

            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(viewHolder.itemView.getContext());

            // Request a string response from the rvioRequest URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, rvioRequest,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!response.isEmpty()){
                                viewHolder.tvPackageLastVersion.setText("Last: " + response.substring(0, 7));
                                versionChecked = true;
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d(Constants.TAG, error.getLocalizedMessage());
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }

    }

    @Override
    public void unbindView(@NonNull ViewHolder holder) {
        super.unbindView(holder);
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    /**
     * our ViewHolder
     */
    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected FrameLayout view;
        @BindView(R.id.img_package_updateStatus)
        ImageView imgUpdateStatus;
        @BindView(R.id.tv_package_title)
        TextView tvPackageTitle;
        @BindView(R.id.tv_package_local_version)
        TextView tvPackageLocalVersion;
        @BindView(R.id.tv_package_last_version)
        TextView tvPackageLastVersion;
        @BindView(R.id.lnLyt_package_download)
        LinearLayout lytPackageDownload;
        @BindView(R.id.tv_package_download_speed)
                TextView tvDownloadSpeed;
        @BindView(R.id.tv_package_download_status)
                TextView tvDownloadStatus;
        @BindView(R.id.tv_package_download_progress)
                TextView tvDownloadProgress;
        @BindView(R.id.tv_package_time_remaining)
                TextView tvTimeRemaining;
        @BindView(R.id.bar_package_progress)
        ProgressBar barDownloadProgress;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.view = (FrameLayout) view;

        }
    }
}
