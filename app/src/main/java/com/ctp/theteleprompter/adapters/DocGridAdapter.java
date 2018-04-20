package com.ctp.theteleprompter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctp.theteleprompter.R;
import com.ctp.theteleprompter.model.Doc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DocGridAdapter extends RecyclerView.Adapter<DocGridAdapter.DocGridViewHolder>{

    private DocGridAdapterCallbacks mCallback;
    private Cursor cursor;

    private List<Doc> docList;

    public DocGridAdapter(DocGridAdapterCallbacks mCallback, Cursor cursor) {
        this.mCallback = mCallback;
        this.cursor = cursor;
        docList = new ArrayList<>();
    }

    public interface DocGridAdapterCallbacks{
        void onDocClicked(Doc clickedDoc, CardView view);
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

        Doc doc = docList.get(position);


        holder.docTitle.setText(doc.getTitle());
        holder.docPreview.setText(doc.getText());
        holder.itemView.setTag(doc.getId());

    }

    public void deletePosition(int position){
        notifyItemRemoved(position);
    }

    public Doc getDocAtPosition(int position){
        return docList.get(position);

    }


    public void move(int fromPosition, int toPosition){
        Collections.swap(docList, fromPosition, toPosition);
        notifyItemMoved(fromPosition,toPosition);


    }


    public List<Doc> getDocList() {
        return docList;
    }

    @Override
    public int getItemCount() {

        if(docList==null)
            return 0;
        return docList.size();

    }

    public void swapCursor(Cursor newCursor){

        if(cursor!=null){
            cursor = null;
        }
        cursor = newCursor;
        populateListFromCursor();

    }


    private void populateListFromCursor(){

        if(cursor!=null) {
            docList = new ArrayList<>();

            while (cursor.moveToNext()) {
                Doc d = new Doc(cursor);
                docList.add(d);
            }

        }
        else {
            docList = null;
        }

        notifyDataSetChanged();

    }




    public class DocGridViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener{

        @BindView(R.id.doc_title)
        TextView docTitle;

        @BindView(R.id.doc_text_preview)
        TextView docPreview;

        @BindView(R.id.grid_card_view)
        CardView cardView;

        public DocGridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this,itemView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            Doc doc = new Doc(cursor);
            mCallback.onDocClicked(doc,(CardView) view);
        }
    }
}
