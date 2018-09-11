package com.example.moham.simplewaitlist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moham.simplewaitlist.Data.WaitListContract;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.GuestViewHolder>  {



    Context context;
    Cursor cursor;

    public GuestAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public GuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view  = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
        return new GuestViewHolder(view);

    }

    @Override
    public void onBindViewHolder(GuestViewHolder holder, int position) {


        if(!cursor.moveToPosition(position)){
            return;
        }

        String name  = cursor.getString(cursor.getColumnIndex(WaitListContract.WaitListEntry.COLUMN_GUEST_NAME));
        String size  = cursor.getString(cursor.getColumnIndex(WaitListContract.WaitListEntry.COLUMN_PARTY_SIZE));
        long id = cursor.getLong(cursor.getColumnIndex(WaitListContract.WaitListEntry._ID));

        holder.name.setText(name);
        holder.size.setText(size);
        holder.itemView.setTag(id);


    }


    @Override
    public int getItemCount() {

        return cursor.getCount();

    }

    public static class GuestViewHolder extends RecyclerView.ViewHolder{

        TextView name,size;

        public GuestViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.guest_name);
            size = itemView.findViewById(R.id.party_size);
        }
    }

    public void swapCursor(Cursor newCursor){
        if (cursor != null){
            cursor.close();
        }

        cursor = newCursor;

        if(newCursor != null){
            //Force RV to refresh
            this.notifyDataSetChanged();
        }

    }
}
