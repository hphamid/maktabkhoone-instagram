package ir.hphamid.instagram.list;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created on 1/26/17 at 12:42 PM.
 * Project: instagram
 *
 * @author hamid
 */

public abstract class EndlessAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public abstract int getDataItemsCount();
    public abstract VH onDataCreateViewHolder(ViewGroup parent, int viewType);
    public abstract void onDataBindViewHolder(VH holder, int position);
    protected abstract int getLoadingViewType();
    protected abstract void loadMore(int position , LoadMoreCallback callback);
    protected void onDataReset(){}
    public int getDataItemViewType(int position){
        return 0;
    }

    public static class DefaultLoadingViewHolder extends RecyclerView.ViewHolder{

        public DefaultLoadingViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class LoadMoreCallback {
        private int position;
        private Context context;
        public LoadMoreCallback(int position, Context context){
            this.position = position;
            this.context = context;
        }
        public int getPosition(){
            return position;
        }
        public void done(final int count){
            Handler handler = new Handler(context.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    EndlessAdapter.this.setLoadingDone(position, count);
                }
            });
        }
    }

    public final static int LoadingViewType = -1;

    private boolean loading;
    private boolean done;
    private int space;

    public EndlessAdapter(){
        this(5);
    }

    public EndlessAdapter(int space){
        loading = false;
        done = false;
        this.space = space;
    }

    public boolean isLoading() {
        return loading;
    }

    /**
     * must be called from main thread
     */
    private void setLoading() {
        if(this.isLoading()){
            return;
        }
        this.loading = true;
    }

    public boolean isDone() {
        return done;
    }


    public void setLoadingDone(int position, int count){
        if(!isLoading()){
            return;
        }
        if(count <= 0){
            done = true;
            count--;
        }
        count++;
        notifyItemRemoved(position + 1);
        notifyItemRangeInserted(position, count);
        loading = false;
    }

    public void reset(){
        loading = false;
        done = false;
        onDataReset();
        notifyDataSetChanged();
    }

    /**
     * override if you need to make custom loading view holder
     */
    protected RecyclerView.ViewHolder getLoadingViewHolder(ViewGroup parent){
        View view = LayoutInflater.from(parent.getContext()).inflate(getLoadingViewType(), parent, false);
        return new DefaultLoadingViewHolder(view);
    }

    /**
     * override if you need to do custom modification on loading view bind
     */
    protected void onLoadingBindViewHolder(RecyclerView.ViewHolder holder){
        //nothing!
    }

    protected void askToLoadMore(Context context){
        if(isLoading() || isDone()){
            return;
        }
        setLoading();
        LoadMoreCallback callback = new LoadMoreCallback(getDataItemsCount(), context);
        this.loadMore(callback.getPosition(), callback);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == LoadingViewType){
            return getLoadingViewHolder(parent);
        }else{
            return onDataCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(position >= (getDataItemsCount() - space)){
            askToLoadMore(holder.itemView.getContext());
        }
        if(holder.getItemViewType() == LoadingViewType){
            onLoadingBindViewHolder(holder);
        }else{
            onDataBindViewHolder((VH)holder, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isDone()){
            return getDataItemViewType(position);
        }else{
            if(position == getItemCount() - 1){
                return LoadingViewType;
            }else{
                return getDataItemViewType(position);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(isDone()){
            return getDataItemsCount();
        }else{
            return getDataItemsCount() + 1;
        }
    }
}
