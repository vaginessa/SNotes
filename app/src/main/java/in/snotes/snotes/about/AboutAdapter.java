package in.snotes.snotes.about;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.snotes.snotes.R;
import in.snotes.snotes.model.AboutDescModel;
import in.snotes.snotes.model.AboutModel;


public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> aboutItems;
    private Context context;
    private AboutListener mListener;

    private static final int ABOUT_ITEM_WITH_DESC = 0;
    private static final int ABOUT_ITEM_WITHOUT_DESC = 1;

    public AboutAdapter(Context context, AboutListener listener, List<Object> aboutItems) {
        this.aboutItems = aboutItems;
        this.context = context;
        mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ABOUT_ITEM_WITH_DESC) {
            View v = LayoutInflater.from(context).inflate(R.layout.item_about_desc, parent, false);
            return new AboutDescViewHolder(v);
        } else {
            View v = LayoutInflater.from(context).inflate(R.layout.item_about, parent, false);
            return new AboutViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == ABOUT_ITEM_WITH_DESC) {
            AboutDescModel item = (AboutDescModel) aboutItems.get(position);
            ((AboutDescViewHolder) holder).bind(item);
        } else {
            AboutModel item = (AboutModel) aboutItems.get(position);
            ((AboutViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (aboutItems.get(position) instanceof AboutDescModel) {
            return ABOUT_ITEM_WITH_DESC;
        } else {
            return ABOUT_ITEM_WITHOUT_DESC;
        }
    }

    @Override
    public int getItemCount() {
        return aboutItems.size();
    }

    public interface AboutListener {
        void onItemClicked(String title);
    }

    public class AboutDescViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon_about_desc)
        ImageView icon;
        @BindView(R.id.tv_about_title_desc)
        TextView title;
        @BindView(R.id.tv_about_desc)
        TextView desc;

        public AboutDescViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                String title = ((AboutDescModel) aboutItems.get(getAdapterPosition())).getTitle();
                mListener.onItemClicked(title);
            });

        }

        public void bind(AboutDescModel item) {
            icon.setImageResource(item.getIcon());
            title.setText(item.getTitle());
            desc.setText(item.getDesc());
        }
    }

    public class AboutViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.icon_about)
        ImageView icon;
        @BindView(R.id.tv_about)
        TextView title;

        public AboutViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                String title = ((AboutModel) aboutItems.get(getAdapterPosition())).getTitle();
                mListener.onItemClicked(title);
            });

        }

        public void bind(AboutModel item) {
            icon.setImageResource(item.getIcon());
            title.setText(item.getTitle());
        }
    }


}
