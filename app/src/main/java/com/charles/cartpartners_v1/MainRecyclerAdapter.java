package com.charles.cartpartners_v1;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.charles.cookingapp.R;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> {

    private ArrayList<Item> itemListings;

    public MainRecyclerAdapter(ArrayList<Item> itemListings) {
        this.itemListings = itemListings;
    }

    @Override
    public MainRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_recycler_elements, parent, false);

        return new MainRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainRecyclerAdapter.ViewHolder holder, final int position) {
        //get the information stored in the arraylists, then make this element look nice
        final String name = itemListings.get(position).getName();
        holder.name.setText(name);
        final String type = itemListings.get(position).getType();
        final String quantity = "" + itemListings.get(position).getQuantity();
        final String result = type + " | Qty: " + quantity;
        holder.cuisine.setText(result);
        final String dollar = "" + itemListings.get(position).getPrice();
        holder.dollars.setText(dollar);

        //TODO: BELOW: these need to be added back in later
        /*
        final String description = descriptions.get(position);
        //snips the description smaller so it fits in the view well
        String shortened_description = description;
        if (description.length() > 40) {
            shortened_description = description.substring(0, 40);
            shortened_description += "...";
        }
        holder.description.setText(shortened_description);
        */

        /*
        //TESTING: getting the images to work
        final String imageName = images.get(position);
        Resources resources = holder.name.getContext().getResources();
        int resourceID = resources.getIdentifier(imageName, "drawable", holder.name.getContext().getPackageName());  //don't want the .png suffix etc. on the imageName
        holder.imageView.setImageResource(resourceID);

        //you can click on the picture of the item and it will redirect you to a page about it
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(v.getContext(), RecipeActivity.class);
                //stores all the necessary data in the intent
                i.putExtra("Name", name);
                i.putExtra("Description", description);
                i.putExtra("imageName", imageName);
                v.getContext().startActivity(i);
            }
        });
        */

        holder.sale_button.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Sale Button", Toast.LENGTH_SHORT).show();

                //prompt for input on the new sale price, as well as the sale duration, then press ok or cancel button
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
                LayoutInflater inflater = LayoutInflater.from(v.getContext());
                dialogBuilder.setView(inflater.inflate(R.layout.sale_dialog, null));

                //data we're collecting
                final int[] startDate = new int[5]; //goes month, day, year, then hour, minute (hour is in 24-hour time)
                final boolean[] startTimeEntered = new boolean[1];


                //TODO: use the collected data in the API call
                //add the action buttons
                dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //call the API for changing the sale price

                    }
                });

                dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

                AlertDialog salePrompt = dialogBuilder.create();
                salePrompt.show();

                //1. get the start date and time settled
                final TextView saleStart = salePrompt.findViewById(R.id.sale_start);
                saleStart.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        //only let the user enter the time once
                        if (startTimeEntered[0]) {
                            Toast.makeText(view.getContext(), "Error: Can Only Enter Start Time Once", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        //since a "click" is an ACTION_DOWN followed by an ACTION_UP
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            DatePickerDialog datePicker = new DatePickerDialog(view.getContext(), R.style.AlertDialogTheme);
                            datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                    startDate[2] = i;
                                    startDate[0] = i1;
                                    startDate[1] = i2;

                                    //now make a Time dialog popup next
                                    TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                            startDate[3] = i;
                                            startDate[4] = i1;
                                            Timestamp startTimeStamp = new Timestamp(startDate[2] - 1900, startDate[0], startDate[1], startDate[3], startDate[4], 0, 0);

                                            Calendar calendar = Calendar.getInstance();
                                            Timestamp currTimestamp = new Timestamp(calendar.getTime().getTime());
                                            //do not let the user input a time range that is before current time
                                            if (startTimeStamp.before(currTimestamp)) {
                                                Toast.makeText(timePicker.getContext(), "Error: Start Time Before Current Time", Toast.LENGTH_SHORT).show();
                                            } else {
                                                saleStart.setText(startTimeStamp.toString().substring(0, 16));
                                                startTimeEntered[0] = true;
                                            }
                                        }
                                    };

                                    TimePickerDialog timePicker = new TimePickerDialog(datePicker.getContext(), listener, 12, 0, false);
                                    timePicker.show();
                                }
                            });

                            datePicker.show();
                        }

                        return false;
                    }

                });


                //2. get the end date and time settled
                //ensure the end date is NOT before the start date OR before the current time
                final TextView saleEnd = salePrompt.findViewById(R.id.sale_end);
                final int[] endDate = new int[5]; //goes month, day, year, then hour, minute (hour is in 24-hour time)
                final boolean[] endTimeEntered = new boolean[1];
                saleEnd.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        //need to enter the start time before anything else
                        if (!startTimeEntered[0]) {
                            Toast.makeText(view.getContext(), "Error: Please Enter Start Time First", Toast.LENGTH_SHORT).show();
                            return false;
                            //can only enter end time once
                        } else if (endTimeEntered[0]) {
                            Toast.makeText(view.getContext(), "Error: Can Only Enter End Time Once", Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        //since a "click" is an ACTION_DOWN followed by an ACTION_UP
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                            DatePickerDialog datePicker = new DatePickerDialog(view.getContext(), R.style.AlertDialogTheme);
                            datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                    endDate[2] = i;
                                    endDate[0] = i1;
                                    endDate[1] = i2;

                                    //now make a Time dialog popup next
                                    TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                                            endDate[3] = i;
                                            endDate[4] = i1;

                                            //the date array goes month, day, year, then hour, minute (hour is in 24-hour time)
                                            Timestamp endTimeStamp = new Timestamp(endDate[2] - 1900, endDate[0], endDate[1], endDate[3], endDate[4], 0, 0);
                                            Timestamp startTimeStamp = new Timestamp(startDate[2] - 1900, startDate[0], startDate[1], startDate[3], startDate[4], 0, 0);
                                            //do not let the user input a time range that is before current time
                                            if (endTimeStamp.before(startTimeStamp)) {
                                                Toast.makeText(timePicker.getContext(), "Error: End Time Before Start Time", Toast.LENGTH_SHORT).show();
                                            } else {
                                                saleEnd.setText(endTimeStamp.toString().substring(0, 16));
                                                endTimeEntered[0] = true;
                                            }
                                        }
                                    };

                                    TimePickerDialog timePicker = new TimePickerDialog(datePicker.getContext(), listener, 12, 0, false);
                                    timePicker.show();
                                }
                            });

                            datePicker.show();
                        }

                        return false;
                    }

                });

                final EditText salePrice = salePrompt.findViewById(R.id.new_price);
                salePrice.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});

                //TODO: make the API call to take the start timestamp, end timestamp, and new sale price

                //TODO: make an API call to fetch all the item listings data, now that it's updated

            }
        });


        //setup the delete button
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ask "are you sure?"
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                System.out.println("clicked yes");
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //Clicked cancel
                                System.out.println("clicked cancel");
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("Are you sure?").setNegativeButton("Cancel", dialogClickListener)
                        .setPositiveButton("Yes", dialogClickListener).show();

                //ToDo: make the API call to delete the item selected

                //ToDo: make an API call to fetch all the item listings data, now that it's updated

            }
        });


    }


    //code from https://stackoverflow.com/questions/17423483/how-to-limit-edittext-length-to-7-integers-and-2-decimal-places/21802109
    public class DecimalDigitsInputFilter implements InputFilter {
        Pattern mPattern;
        DecimalDigitsInputFilter(int digitsBeforeZero,int digitsAfterZero) {
            mPattern=Pattern.compile("[0-9]{0," + (digitsBeforeZero-1) + "}+((\\.[0-9]{0," + (digitsAfterZero-1) + "})?)|(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher=mPattern.matcher(dest);
            if(!matcher.matches())
                return "";
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return itemListings.size();
    }


    public void remove(int position) {
        itemListings.remove(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_picture)
        ImageView imageView;
        @BindView(R.id.recipe_short_description)
        TextView description;
        @BindView(R.id.recipe_cuisine)
        TextView cuisine;
        @BindView(R.id.item_price)
        TextView dollars;
        @BindView(R.id.item_name)
        TextView name;

        @BindView(R.id.delete_button)
        ImageButton delete_button;
        @BindView(R.id.sale_button)
        ImageButton sale_button;


        ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);

        }

    }

}
