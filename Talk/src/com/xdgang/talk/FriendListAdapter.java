/**
 * 
 */

package com.xdgang.talk;

import com.renren.api.connect.android.friends.FriendsGetFriendsResponseBean.Friend;
import com.xdgang.talk.activity.FriendListActivity.AdapterHandler;
import com.xdgang.talk.helper.FileDownload;
import com.xdgang.talk.helper.FileDownload.IFileDownloadCallback;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * @author Rico
 */
public class FriendListAdapter extends BaseAdapter {

    private ArrayList<Friend> items = new ArrayList<Friend>();

    private AdapterHandler handler;

    public FriendListAdapter(ArrayList<Friend> items, AdapterHandler handler) {
        // TODO Auto-generated constructor stub
        this.items = items;
        this.handler = handler;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        if (items != null && position >= 0 && position < items.size()) {
            return items.get(position);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (items != null && position >= 0 && position < items.size()) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.conversion_item, null);
                holder = new ViewHolder();
                holder.nickname = (TextView) convertView.findViewById(R.id.nickname_tv);
                holder.latestMsg = (TextView) convertView.findViewById(R.id.last_msg_tv);
                holder.msgDate = (TextView) convertView.findViewById(R.id.update_time_tv);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar_iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Friend item = items.get(position);
            holder.nickname.setText(item.getName());
            holder.latestMsg.setText(String.valueOf(item.getUid()));
            String headurl = item.getHeadurl();
            String picName = headurl.substring(headurl.lastIndexOf("/") + 1);
            String localPath = context.getFilesDir() + "avatar/";
            File localPic = new File(localPath + picName);
            if (localPic.exists() && localPic.isFile()) {
                Bitmap bit = BitmapFactory.decodeFile(localPath + picName);
                int width = bit.getWidth();
                int height = bit.getHeight();
                DisplayMetrics metrics = context.getApplicationContext().getResources()
                        .getDisplayMetrics();
                int disWidth = metrics.widthPixels;
                Bitmap bit_ = Bitmap.createScaledBitmap(bit, width * disWidth / 800, height
                        * disWidth / 800, true);
                holder.avatar.setImageBitmap(bit_);
            } else {
                holder.avatar.setImageResource(R.drawable.mini_avatar_shadow);
                download(localPath, headurl, picName);
            }

        }
        return convertView;
    }

    private void download(String path, String headurl, String picName) {
        // TODO Auto-generated method stub
        FileDownload.instance().setDownloadDir(path);
        FileDownload.instance().Download(headurl, new IFileDownloadCallback() {

            @Override
            public void onPercentage(int dID, long len, int curr) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onFaild(int dID, float percent, int errCode) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onComplete(int dID, String url, String localFile) {
                // TODO Auto-generated method stub
                handler.sendEmptyMessage(AdapterHandler.REFRESH_AVATER);
            }
        }, picName);
    }

    static class ViewHolder {
        TextView nickname;

        TextView latestMsg;

        TextView msgDate;

        ImageView avatar;
    }
}
