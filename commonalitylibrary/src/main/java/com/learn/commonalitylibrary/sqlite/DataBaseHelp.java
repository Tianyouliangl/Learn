package com.learn.commonalitylibrary.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.learn.commonalitylibrary.ChatMessage;
import com.learn.commonalitylibrary.Constant;
import com.learn.commonalitylibrary.LoginBean;
import com.learn.commonalitylibrary.SessionMessage;
import com.learn.commonalitylibrary.util.GsonUtil;
import com.learn.commonalitylibrary.util.OfTenUtils;
import com.white.easysp.EasySP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBaseHelp {

    private HashMap<String, Long> timeMap = new HashMap();

    private static DataBaseHelp helper;
    private SQLiteDatabase writableDatabase;
    private Context mContext;

    public DataBaseHelp(Context context) {
        mContext = context;
        DataBaseSQLite sqLite = new DataBaseSQLite(context);
        writableDatabase = sqLite.getWritableDatabase();
    }

    public static DataBaseHelp getInstance(Context context) {
        if (helper == null) {
            synchronized (DataBaseHelp.class) {
                if (helper == null) {
                    DataBaseHelp mInstance = new DataBaseHelp(context);
                    helper = mInstance;
                }
            }
        }
        return helper;
    }

    private void createHistoryTable(String tableName) {
        String uid = OfTenUtils.replace(EasySP.init(mContext).getString(Constant.SPKey_UID));
        String sql = "CREATE TABLE IF NOT EXISTS history_" + tableName + "_" + uid + " (" +
                "id INTEGER PRIMARY KEY autoincrement," +
                "from_id VARCHAR (255)," +
                "to_id VARCHAR (255)," +
                "pid VARCHAR (255)," +
                "body TEXT," +
                "conversation VARCHAR (255)," +
                "body_type INTEGER (11)," +
                "msg_status INTEGER (11)," +
                "type INTEGER (11)," +
                "displaytime INTEGER (11)," +
                "time BIGINT(20)" + ")";
        writableDatabase.execSQL(sql);
    }

    public void createSessions() {
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String sql = "CREATE TABLE IF NOT EXISTS sessions_" + OfTenUtils.replace(uid) + " (" +
                "id INTEGER PRIMARY KEY autoincrement," +
                "conversation VARCHAR (255)," +
                "to_id VARCHAR (255)," +
                "from_id VARCHAR (255)," +
                "body VARCHAR (255)," +
                "time BIGINT(20)," +
                "msg_status INTEGER (11)," +
                "body_type INTEGER (11)," +
                "number INTEGER (11)" + ")";
        writableDatabase.execSQL(sql);
    }

    public void createUserTable() {
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String sql = "CREATE TABLE IF NOT EXISTS user_" + OfTenUtils.replace(uid) + " (" +
                "id INTEGER PRIMARY KEY autoincrement," +
                "data TEXT," + "uid TEXT" + ")";
        writableDatabase.execSQL(sql);
    }

    public Boolean addChatMessage(ChatMessage chatMessage) {
        String uid = OfTenUtils.replace(EasySP.init(mContext).getString(Constant.SPKey_UID));
        String query_sql = "SELECT * FROM history_" + chatMessage.getConversation() + "_" + uid + " where pid = " + "'" + chatMessage.getPid() + "'";
        createHistoryTable(chatMessage.getConversation());
        ContentValues values = new ContentValues();
        values.put("from_id", chatMessage.getFromId());
        values.put("to_id", chatMessage.getToId());
        values.put("pid", chatMessage.getPid());
        values.put("body", chatMessage.getBody());
        values.put("conversation", chatMessage.getConversation());
        values.put("body_type", chatMessage.getBodyType());
        values.put("msg_status", chatMessage.getMsgStatus());
        values.put("type", chatMessage.getType());
        values.put("displaytime", chatMessage.getDisplaytime());
        values.put("time", chatMessage.getTime());
        Cursor cursor = writableDatabase.rawQuery(query_sql, null);
        if (!cursor.moveToNext()) {
            long l = writableDatabase.insert("history_" + chatMessage.getConversation() + "_" + uid, null, values);
            if (l > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            int update = writableDatabase.update("history_" + chatMessage.getConversation() + "_" + uid, values, "pid = ?", new String[]{"" + chatMessage.getPid()});
            if (update > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public Boolean isAddChatMessage(ChatMessage chatMessage){
        createHistoryTable(chatMessage.getConversation());
        String uid = OfTenUtils.replace(EasySP.init(mContext).getString(Constant.SPKey_UID));
        String query_sql = "SELECT * FROM history_" + chatMessage.getConversation() + "_" + uid + " where pid = " + "'" + chatMessage.getPid() + "'";
        Cursor cursor = writableDatabase.rawQuery(query_sql, null);
        return cursor.moveToNext();
    }

    public void updateChatMessage(ChatMessage chatMessage) {
        String uid = OfTenUtils.replace(EasySP.init(mContext).getString(Constant.SPKey_UID));
        createHistoryTable(chatMessage.getConversation());
        ContentValues values = new ContentValues();
        values.put("from_id", chatMessage.getFromId());
        values.put("to_id", chatMessage.getToId());
        values.put("pid", chatMessage.getPid());
        values.put("body", chatMessage.getBody());
        values.put("conversation", chatMessage.getConversation());
        values.put("body_type", chatMessage.getBodyType());
        values.put("msg_status", chatMessage.getMsgStatus());
        values.put("type", chatMessage.getType());
        values.put("displaytime", chatMessage.getDisplaytime());
        values.put("time", chatMessage.getTime());
        writableDatabase.update("history_" + chatMessage.getConversation() + "_" + uid, values, "pid =?", new String[]{"" + chatMessage.getPid()});
    }

    public List<ChatMessage> getChatMessage(String conversation, int pageNo, int pageSize) {
        String uid = OfTenUtils.replace(EasySP.init(mContext).getString(Constant.SPKey_UID));
        String sql = null;
        List<ChatMessage> chatMessageList = new ArrayList<>();
        createHistoryTable(conversation);
        Long mTime = timeMap.get(conversation); //tableName = conversation
        if (pageNo > 1) {
            if (mTime != null) {
                sql = "SELECT * FROM ( SELECT * FROM history_" + conversation + "_" + uid + " WHERE conversation = " + "'" + conversation + "'" + " AND time <" + mTime + " ORDER BY time DESC LIMIT " + pageSize + ") aa" + " ORDER BY time";
            } else {
                sql = "SELECT * FROM ( SELECT * FROM history_" + conversation + "_" + uid + " WHERE conversation = " + "'" + conversation + "'" + " ORDER BY time DESC LIMIT " + (pageNo - 1) * pageSize + "," + pageSize + ") aa" + " ORDER BY time";
            }
        } else {
            sql = "SELECT * FROM ( SELECT * FROM history_" + conversation + "_" + uid + " WHERE conversation = " + "'" + conversation + "'" + " ORDER BY time DESC LIMIT " + (pageNo - 1) * pageSize + "," + pageSize + ") aa" + " ORDER BY time";
        }

        Cursor cursor = writableDatabase.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            ChatMessage message = new ChatMessage();
            String from_id = cursor.getString(cursor.getColumnIndex("from_id"));
            String to_id = cursor.getString(cursor.getColumnIndex("to_id"));
            String pid = cursor.getString(cursor.getColumnIndex("pid"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            String ct = cursor.getString(cursor.getColumnIndex("conversation"));
            int body_type = cursor.getInt(cursor.getColumnIndex("body_type"));
            int msg_status = cursor.getInt(cursor.getColumnIndex("msg_status"));
            int type = cursor.getInt(cursor.getColumnIndex("type"));
            int displaytime = cursor.getInt(cursor.getColumnIndex("displaytime"));
            Long time = cursor.getLong(cursor.getColumnIndex("time"));
            message.setFromId(from_id);
            message.setToId(to_id);
            message.setPid(pid);
            message.setBody(body);
            message.setConversation(ct);
            message.setBodyType(body_type);
            message.setMsgStatus(msg_status);
            message.setType(type);
            message.setDisplaytime(displaytime);
            message.setTime(time);
            chatMessageList.add(message);
        }
        return chatMessageList;
    }


    public void addOrUpdateSession(SessionMessage sessionMessage) {
        ContentValues values = new ContentValues();
        values.put("conversation", sessionMessage.getConversation());
        values.put("to_id", sessionMessage.getTo_id());
        values.put("from_id",sessionMessage.getFrom_id());
        values.put("body", sessionMessage.getBody());
        values.put("time", sessionMessage.getTime());
        values.put("msg_status", sessionMessage.getMsg_status());
        values.put("number", sessionMessage.getNumber());
        values.put("body_type", sessionMessage.getBody_type());
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String query_sql = "SELECT * FROM sessions_" + OfTenUtils.replace(uid) + " where conversation = " + "'" + sessionMessage.getConversation() + "'";
        Cursor cursor = writableDatabase.rawQuery(query_sql, null);
        if (cursor.moveToNext()) {
            writableDatabase.update("sessions_" + OfTenUtils.replace(uid), values, "conversation =?", new String[]{"" + sessionMessage.getConversation()});
        } else {
            writableDatabase.insert("sessions_" + OfTenUtils.replace(uid), null, values);
        }
    }

    public void addOrUpdateUser(String id, String json) {
        ContentValues values = new ContentValues();
        values.put("data", json);
        values.put("uid", id);
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String query_sql = "SELECT * FROM user_" + OfTenUtils.replace(uid) + " where uid = " + "'" + id + "'";
        Cursor cursor = writableDatabase.rawQuery(query_sql, null);
        if (cursor.moveToNext()) {
            writableDatabase.update("user_" + OfTenUtils.replace(uid), values, "uid =?", new String[]{"" + id});
        } else {
            writableDatabase.insert("user_" + OfTenUtils.replace(uid), null, values);
        }
    }

    public LoginBean getUserData(String id) {
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String query_sql = "SELECT * FROM user_" + OfTenUtils.replace(uid) + " where uid = " + "'" + id + "'";
        Cursor cursor = writableDatabase.rawQuery(query_sql, null);
        if (cursor.moveToNext()) {
            String json = cursor.getString(cursor.getColumnIndex("data"));
            LoginBean loginBean = GsonUtil.GsonToBean(json, LoginBean.class);
            return loginBean;
        }
        return null;
    }

    public int getSessionNumber(String conversation) {
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String query_sql = "SELECT * FROM sessions_" + OfTenUtils.replace(uid) + " where conversation = " + "'" + conversation + "'";
        Cursor cursor = writableDatabase.rawQuery(query_sql, null);
        if (cursor.moveToNext()) {
            return cursor.getInt(cursor.getColumnIndex("number"));
        }
        return 0;
    }

    public void setSessionNumber(String conversation, int number) {
        ContentValues values = new ContentValues();
        values.put("number", number);
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String query_sql = "SELECT * FROM sessions_" + OfTenUtils.replace(uid) + " where conversation = " + "'" + conversation + "'";
        Cursor cursor = writableDatabase.rawQuery(query_sql, null);
        if (cursor.moveToNext()) {
            writableDatabase.update("sessions_" + OfTenUtils.replace(uid), values, "conversation =?", new String[]{"" + conversation});
        }
    }

    public void deleteSessionConversation(String conversation) {
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String query_sql = "DELETE FROM sessions_" + OfTenUtils.replace(uid) + " where conversation = " + "'" + conversation + "'";
        writableDatabase.execSQL(query_sql);
    }

    public List<SessionMessage> getSessionList() {
        List<SessionMessage> list = new ArrayList<>();
        String uid = EasySP.init(mContext).getString(Constant.SPKey_UID);
        String query_sql = "SELECT * FROM sessions_" + OfTenUtils.replace(uid) + " ORDER BY time DESC";
        Cursor cursor = writableDatabase.rawQuery(query_sql, null);
        while (cursor.moveToNext()) {
            SessionMessage sessionMessage = new SessionMessage();
            String conversation = cursor.getString(cursor.getColumnIndex("conversation"));
            String to_id = cursor.getString(cursor.getColumnIndex("to_id"));
            String from_id = cursor.getString(cursor.getColumnIndex("from_id"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            long time = cursor.getLong(cursor.getColumnIndex("time"));
            int msg_status = cursor.getInt(cursor.getColumnIndex("msg_status"));
            int number = cursor.getInt(cursor.getColumnIndex("number"));
            int body_type = cursor.getInt(cursor.getColumnIndex("body_type"));
            sessionMessage.setConversation(conversation);
            sessionMessage.setTo_id(to_id);
            sessionMessage.setFrom_id(from_id);
            sessionMessage.setBody(body);
            sessionMessage.setTime(time);
            sessionMessage.setMsg_status(msg_status);
            sessionMessage.setNumber(number);
            sessionMessage.setBody_type(body_type);
            list.add(sessionMessage);
        }
        return list;
    }
}
