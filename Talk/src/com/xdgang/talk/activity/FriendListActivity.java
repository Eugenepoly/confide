/**
 * 
 */

package com.xdgang.talk.activity;

import com.renren.api.connect.android.AsyncRenren;
import com.renren.api.connect.android.common.AbstractRequestListener;
import com.renren.api.connect.android.exception.RenrenError;
import com.renren.api.connect.android.friends.FriendsGetFriendsRequestParam;
import com.renren.api.connect.android.friends.FriendsGetFriendsResponseBean;
import com.renren.api.connect.android.friends.FriendsGetFriendsResponseBean.Friend;
import com.xdgang.talk.FriendListAdapter;
import com.xdgang.talk.R;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author Administrator
 */
public class FriendListActivity extends BaseActivity {

    protected static final String TAG = "FriendListActivity";

    /**
     * 好友列表
     */
    private ArrayList<Friend> friendList;

    private static FriendListAdapter friendListAdapter;

    private static ListView friendListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        titleText.setText("通讯录");
        friendListView = (ListView) getLayoutInflater().inflate(R.layout.friend_list, null);
        root.addView(friendListView);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (renren != null) {
            AsyncRenren asyncRenren = new AsyncRenren(renren);
            FriendsGetFriendsRequestParam param = new FriendsGetFriendsRequestParam();
            AbstractRequestListener<FriendsGetFriendsResponseBean> listener = new AbstractRequestListener<FriendsGetFriendsResponseBean>() {

                @Override
                public void onComplete(final FriendsGetFriendsResponseBean bean) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            friendList = bean.getFriendList();
                            if (friendList != null && friendList.size() > 0) {
                                AdapterHandler handler = new AdapterHandler(mContext);
                                friendListAdapter = new FriendListAdapter(friendList, handler);
                                friendListView.setAdapter(friendListAdapter);
                                // handler.sendEmptyMessage(AdapterHandler.SET_ADAPTER);
                            }
                            Log.e(TAG, friendList.toString());
                        }
                    });
                }

                @Override
                public void onRenrenError(final RenrenError renrenError) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Log.e(TAG, renrenError.toString());
                        }
                    });
                }

                @Override
                public void onFault(final Throwable fault) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                        }
                    });
                }
            };
            asyncRenren.getFriends(param, listener);

        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        friendList = null;
        friendListAdapter = null;
        friendListView = null;
    }

    public static class AdapterHandler extends Handler {

        public static final int REFRESH_AVATER = 1;

        public static final int SET_ADAPTER = 2;

        private final WeakReference<Context> weakReference;

        public AdapterHandler(Context context) {
            super();
            // TODO Auto-generated constructor stub
            weakReference = new WeakReference<Context>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_AVATER:
                    if (weakReference == null) {
                        break;
                    }
                    Context context = (Context) weakReference.get();
                    if (context == null) {
                        break;
                    }
                    if (friendListAdapter != null) {
                        friendListAdapter.notifyDataSetChanged();
                    }
                    break;

                case SET_ADAPTER:

                    break;

                default:
                    break;
            }
        }

    }
}
