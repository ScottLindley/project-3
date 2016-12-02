package com.scottlindley.touchmelabs.RecyclerViewComponents;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scottlindley.touchmelabs.ModelObjects.CardContent;
import com.scottlindley.touchmelabs.R;

import java.util.List;

/**
 * Created by jonlieblich on 12/1/16.
 */

public class CardRecyclerViewAdapter extends RecyclerView.Adapter<CardContentViewHolder> {
    private List<CardContent> mCardList;

    public CardRecyclerViewAdapter(List<CardContent> list) {
        mCardList = list;
    }

    @Override
    public CardContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch(viewType) {
            case R.layout.twitter_card:
                return inflater.inflate(R.layout.twitter_card, parent, false);
            case R.layout.news_card:
                return inflater.inflate(R.layout.news_card, parent, false);
            case R.layout.weather_card:
                return inflater.inflate(R.layout.weather_card, parent, false);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(CardContentViewHolder holder, int position) {
        int type = getItemViewType(position);
        switch(type) {
            case R.layout.twitter_card:

                break;
            case R.layout.news_card:

                break;
            case R.layout.weather_card:

                break;
            default:
                Toast.makeText(holder.itemView.getContext(), "Whoops!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }
}
