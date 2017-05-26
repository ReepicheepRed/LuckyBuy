package com.luckybuy.db;

import org.xutils.DbManager;

import java.io.File;

/**
 * Created by zhiPeng.S on 2016/6/29.
 */
public class DB_Config {
    private static DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("lucky_cart.db")
            // 不设置dbDir时, 默认存储在app的私有目录.
            //.setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
            .setDbVersion(2)
            .setDbOpenListener(new DbManager.DbOpenListener() {
                @Override
                public void onDbOpened(DbManager db) {
                    // 开启WAL, 对写入加速提升巨大
                    db.getDatabase().enableWriteAheadLogging();
                }
            })
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    // TODO: ...
                    // db.addColumn(...);
                    // db.dropTable(...);
                    // ...
                    // or
                    // db.dropDb();
                }
            });

    public static DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }
}
