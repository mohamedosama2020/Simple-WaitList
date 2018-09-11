package com.example.moham.simplewaitlist;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moham.simplewaitlist.Data.WaitListContract;
import com.example.moham.simplewaitlist.Data.WaitListDBHelper;

public class MainActivity extends AppCompatActivity {

    //Widgets
    FloatingActionButton add_guest;
    ImageView waitlistImageView;
    RecyclerView recyclerView;

    //Vars
    WaitListDBHelper waitListDBHelper;
    GuestAdapter guestAdapter;
    SQLiteDatabase sqLiteDatabase;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_guest = findViewById(R.id.add_new_guest);
        waitlistImageView = findViewById(R.id.waitList_imageView);
        recyclerView = findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this,DividerItemDecoration.VERTICAL));

        waitListDBHelper = new WaitListDBHelper(getApplicationContext());
        sqLiteDatabase = waitListDBHelper.getWritableDatabase();
        cursor = getAllGuests();

        guestAdapter = new GuestAdapter(getApplicationContext(),cursor);
        recyclerView.setAdapter(guestAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                long id = (long) viewHolder.itemView.getTag();
                removeGuest(id);
                guestAdapter.swapCursor(getAllGuests());

                if (getAllGuests().getCount() == 0){
                    waitlistImageView.setVisibility(View.VISIBLE);
                }
            }
        }).attachToRecyclerView(recyclerView);




        if(cursor.getCount() != 0){
            waitlistImageView.setVisibility(View.GONE);
        }else
        {
            waitlistImageView.setVisibility(View.VISIBLE);
        }

        add_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showCustomDialog();
            }
        });


    }

    private void showCustomDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_new_guest_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;

        final View dialoglayout = getLayoutInflater().inflate(R.layout.layout_new_guest_dialog,(ViewGroup) findViewById(R.id.customDialog));


        final Button add = dialog.findViewById(R.id.addBtn);
        final Button back = dialog.findViewById(R.id.backBtn);
        final EditText guestName = dialog.findViewById(R.id.guestNameEditText);
        final EditText duration = dialog.findViewById(R.id.durationEditText);

        final View layout = getLayoutInflater().inflate(R.layout.custom_toast,(ViewGroup) findViewById(R.id.custom_toast_layout_id));

        TextView text = layout.findViewById(R.id.toast_text);
        text.setTextColor(Color.WHITE);
        text.setText("Enter Valid Data");
        CardView lyt_card = layout.findViewById(R.id.lyt_card);
        lyt_card.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));

        final Toast toast = new Toast(getApplicationContext());

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = guestName.getText().toString();
                String number = duration.getText().toString();

                if (name.length() == 0 || number.length() == 0){

                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();

                }else
                {
                    addNewGuest(name,number);
                    guestAdapter.swapCursor(getAllGuests());
                    waitlistImageView.setVisibility(View.GONE);
                    dialog.dismiss();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                toast.cancel();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

    private Cursor getAllGuests(){
        Cursor cursor = sqLiteDatabase.query(
                WaitListContract.WaitListEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitListContract.WaitListEntry.COLUMN_TIMESTAMP
        );

        return cursor;
    }

    private long addNewGuest(String name,String partySize){

        long i;
        ContentValues cv = new ContentValues();
        cv.put(WaitListContract.WaitListEntry.COLUMN_GUEST_NAME,name);
        cv.put(WaitListContract.WaitListEntry.COLUMN_PARTY_SIZE,partySize);

        i = sqLiteDatabase.insert(WaitListContract.WaitListEntry.TABLE_NAME,null,cv);
        return i;
    }

    private boolean removeGuest(long id){

        return sqLiteDatabase.delete(WaitListContract.WaitListEntry.TABLE_NAME,
                WaitListContract.WaitListEntry._ID+"="+id,
                null)>0;

    }
}
