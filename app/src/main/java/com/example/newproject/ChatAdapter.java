package com.example.newproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

        ChatAdapter(Context context, int resource) {
        super(context, resource);
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if(convertView==null){
        LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_item, parent, false);

        viewHolder = new ViewHolder();
        viewHolder.username = (TextView) convertView.findViewById(R.id.list_item_username);
        viewHolder.message = (TextView) convertView.findViewById(R.id.list_item_message);

        convertView.setTag(viewHolder);
        }else{
        viewHolder = (ViewHolder) convertView.getTag();
        }

        ChatMessage item = getItem(position);
        if(item != null) {
        viewHolder.username.setText(item.getUsername());
        viewHolder.message.setText(item.getMessage());
        }

        return convertView;
        }

private static class ViewHolder{
    TextView username;
    TextView message;
}
}