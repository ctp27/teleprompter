package com.ctp.theteleprompter.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctp.theteleprompter.R;
import com.ctp.theteleprompter.data.SharedPreferenceUtils;
import com.ctp.theteleprompter.model.Doc;
import com.ctp.theteleprompter.services.TeleWidgetService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DocGridAdapter extends RecyclerView.Adapter<DocGridAdapter.DocGridViewHolder>{

    private DocGridAdapterCallbacks mCallback;
    private Cursor cursor;
    private int pinnedId = -1;

    private List<Doc> docList;
    private int tutorialCounter;

    public DocGridAdapter(DocGridAdapterCallbacks mCallback, Cursor cursor) {
        this.mCallback = mCallback;
        this.cursor = cursor;
        docList = new ArrayList<>();
        pinnedId = SharedPreferenceUtils.getPinnedId((Context) mCallback);
        tutorialCounter = 0;
    }

    public interface DocGridAdapterCallbacks{
        void onDocClicked(Doc clickedDoc);
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

        setDocTitleSize(doc,holder);

        holder.docTitle.setText(doc.getTitle());
        holder.docPreview.setText(doc.getText());
        holder.itemView.setTag(doc.getId());

        if(doc.getId() == pinnedId){
            holder.pinButton.setImageResource(R.drawable.ic_office_push_pin_selected);
        }else {
            holder.pinButton.setImageResource(R.drawable.ic_office_push_pin);
        }



        setCardBackgroundColor(doc,holder);


    }

    private void setDocTitleSize(Doc doc, DocGridViewHolder holder) {
        String docTitle = doc.getTitle();
        if(docTitle == null || docTitle.isEmpty()){
            return;
        }
        int size;
        int length = docTitle.length();
        if(length>16 && length<=30){
            size = holder.titleSizeMedium;

        }
        else if(length>30){
            size = holder.titleSizeSmall;
        }
        else {
            size=holder.titleSizeLarge;
        }

        holder.docTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,size);


    }

    private void setCardBackgroundColor(Doc doc, DocGridViewHolder holder) {

        if(doc.isTutorial()){
            if(tutorialCounter>2){
                tutorialCounter =0;
            }
//            Log.d("Blahwah","Entered "+doc.getTitle()+" is "+doc.isTutorial());
            holder.layoutContainer.setBackgroundColor(holder.colors[tutorialCounter]);
            tutorialCounter++;
        }else {
            holder.layoutContainer.setBackgroundColor(Color.WHITE);
        }
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


    public void reinsertDoc(Doc doc, int position){

        if(doc!=null){
            docList.add(position,doc);
        }

        notifyDataSetChanged();
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

        @BindView(R.id.doc_grid_item_constraintlayout)
        ConstraintLayout layoutContainer;

        @BindView(R.id.doc_pin_button)
        ImageView pinButton;


        @BindColor(R.color.lightBlue)
        int colorLightBlue;

        @BindColor(R.color.lightYellow)
        int colorLightYellow;

        @BindColor(R.color.lightGreen)
        int colorLightGreen;

        @BindInt(R.integer.title_size_small)
        int titleSizeSmall;

        @BindInt(R.integer.title_size_medium)
        int titleSizeMedium;

        @BindInt(R.integer.title_size_large)
        int titleSizeLarge;

         int[] colors;


        public DocGridViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            ButterKnife.bind(this,itemView);
            pinButton.setOnClickListener(this);
            colors = new int[]{colorLightYellow,colorLightGreen,colorLightBlue};

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Doc doc = docList.get(position);

            switch (view.getId()){
                case R.id.grid_card_view:

                    /*  Pass the clicked doc to the callbacks   */
                    mCallback.onDocClicked(doc);

                    break;

                case R.id.doc_pin_button:
                    /*  The pin button was clicked  */
                    /* Set the new pinned document ID in shared preferences */
                    SharedPreferenceUtils.setPinnedId(view.getContext(),doc.getId());

                    /*  Set the global pinnedID variable to the new Id  */
                    pinnedId = doc.getId();

                    /*  Notify the data is changed to update ui with new pinned doc */
                    notifyDataSetChanged();

                    /* Update the widget of the new Pinned doc */
                    TeleWidgetService.updateTeleWidgets(view.getContext());

                    break;

            }

        }
    }
}
