package com.ctp.theteleprompter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctp.theteleprompter.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocGridAdapter extends RecyclerView.Adapter<DocGridAdapter.DocGridViewHolder>{

    private DocGridAdapterCallbacks mCallback;

    public DocGridAdapter(DocGridAdapterCallbacks mCallback) {
        this.mCallback = mCallback;
    }

    public interface DocGridAdapterCallbacks{
        void onDocClicked();
    }


    List<DummyClass> dummyData = new ArrayList<>();

    {
        for(int i=0;i<11;i++){
            dummyData.add(new DummyClass("Doc Title "+i,getTextPreview()));
        }

    }

    private String getTextPreview(){
        int random = new Random().nextInt(5);
        String theJob ="text Preview ";
        String returnString= "";
        for(int i=0;i<random;i++){
            theJob +=theJob;
        }

        return theJob;
    }

    @NonNull
    @Override
    public DocGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.doc_grid_item,parent,false);
        return new DocGridViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DocGridViewHolder holder, int position) {

        DummyClass dummyClass = dummyData.get(position);

        holder.docTitle.setText(dummyClass.getTitle());
        holder.docPreview.setText(dummyClass.getSubTitle());
        holder.itemView.setTag(position);

    }

    public void deletePosition(int position){
        dummyData.remove(position);
        notifyDataSetChanged();
    }


    public void move(int fromPosition, int toPosition){
        Collections.swap(dummyData, fromPosition, toPosition);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public int getItemCount() {
        if(dummyData==null){
            return 0;
        }
        return dummyData.size();
    }




    public class DocGridViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{

        @BindView(R.id.doc_title)
        TextView docTitle;

        @BindView(R.id.doc_text_preview)
        TextView docPreview;

        public DocGridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public void onClick(View view) {
            mCallback.onDocClicked();
        }
    }
}
