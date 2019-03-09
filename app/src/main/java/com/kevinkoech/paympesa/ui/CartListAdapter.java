/*
 *
 *  * Copyright (C) 2017 Safaricom, Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.kevinkoech.paympesa.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kevinkoech.paympesa.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created  on 8/1/2017.
 */

public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private List<String> items;
    private Context context;
    private Dialog myDialog;
    PriceTransfer priceTransfer;
    private List<String> item_prices = new ArrayList<>();
    private ArrayList<Integer> prices = new ArrayList<>();

    public CartListAdapter(Context context, List<String> items, List<String> item_prices, PriceTransfer priceTransfer) {
        this.items = items;
        this.context = context;
        this.item_prices = item_prices;
        this.priceTransfer = priceTransfer;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public CartListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.category_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartListAdapter.ViewHolder viewHolder, int i) {
        viewHolder.item_name.setText(items.get(i));
        viewHolder.btn_add_to_cart.setText("Add Kshs " + item_prices.get(i));

        if (items.get(i).equals("Tomatoes"))
            viewHolder.item_image.setImageDrawable(context.getResources().getDrawable(R.drawable.tomatoes));
        else if (items.get(i).equals("Apples"))
            viewHolder.item_image.setImageDrawable(context.getResources().getDrawable(R.drawable.apples));
        else if (items.get(i).equals("Bananas"))
            viewHolder.item_image.setImageDrawable(context.getResources().getDrawable(R.drawable.bananas));
    }

    @Override
    /**
     * tells the Adapter that how many rows are there to display
     */
    public int getItemCount() {
        return items.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView item_image;
        TextView item_name;
        Button btn_add_to_cart;

        public ViewHolder(View view) {
            super(view);

            item_image = (ImageView) view.findViewById(R.id.item_image);
            item_name = (TextView) view.findViewById(R.id.item_name);
            btn_add_to_cart = (Button) view.findViewById(R.id.btn_add_to_cart);

            btn_add_to_cart.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (view.getId() == R.id.btn_add_to_cart) {
                prices.add(Integer.valueOf(btn_add_to_cart.getText().toString().replace("Add Kshs ", "")));
                Toast.makeText(context, String.valueOf("Added: " + items.get(position)), Toast.LENGTH_SHORT).show();
                //Log.e("value added to list", btn_add_to_cart.getText().toString().replace("Add Kshs ", ""));
                //Calling a class priceTransfer.java
                priceTransfer.setPrices(prices);
            }
        }
    }
}
