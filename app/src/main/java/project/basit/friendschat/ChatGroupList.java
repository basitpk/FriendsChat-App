package project.basit.friendschat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by RAJESH on 16-06-2017.
 */

public class ChatGroupList extends BaseAdapter {
    ArrayList<String> chats;
    ArrayList<String> creaters;
    LayoutInflater minflater;

    public ChatGroupList(ArrayList<String> chats, ArrayList<String> creaters,Context context) {
        this.chats = chats;
        this.creaters=creaters;
        minflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return chats.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if(convertView==null)
        {
            convertView=minflater.inflate(R.layout.group_list_item,parent,false);
            holder=new ViewHolder();
            holder.name=(TextView)convertView.findViewById(R.id.group_name);
            holder.iv=(ImageView)convertView.findViewById(R.id.profile);
            holder.admin=(TextView)convertView.findViewById(R.id.groupcreater);
            convertView.setTag(holder);
        }
        else
        {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.name.setText(chats.get(position));
        holder.admin.setText(creaters.get(position));
        return convertView;
    }
    static class ViewHolder{
        ImageView iv;
        TextView name;
        TextView admin;
    }
}
