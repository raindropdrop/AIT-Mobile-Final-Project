package com.shirleyhe.aitfinalproject.adapter;

/**
 * Created by shirleyhe on 12/11/16.
 */
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shirleyhe.aitfinalproject.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import clarifai2.dto.prediction.Concept;

import java.util.ArrayList;
import java.util.List;

public class xSearchHistoryAdapter extends RecyclerView.Adapter<xSearchHistoryAdapter.Holder> {

    static final class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.label) TextView label;
        @BindView(R.id.probability) TextView probability;

        public Holder(View root) {
            super(root);
            ButterKnife.bind(this, root);
        }
    }

    @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_concept, parent, false));
    }

    @Override public void onBindViewHolder(Holder holder, int position) {
        final Concept concept = concepts.get(position);
        holder.label.setText(concept.name() != null ? concept.name() : concept.id());
        holder.probability.setText(String.valueOf(concept.value()));
    }


    @NonNull private List<Concept> concepts = new ArrayList<>();

    public xSearchHistoryAdapter(@NonNull List<Concept> concepts, Context context){

    }

    public xSearchHistoryAdapter setData(@NonNull List<Concept> concepts) {
        this.concepts = concepts;
        notifyDataSetChanged();
        return this;
    }


    @Override public int getItemCount() {
        return concepts.size();
    }
}
