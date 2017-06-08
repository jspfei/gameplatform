package net.tt.charging;

import android.app.Application;

import cb1d.o106b7.mee315.ag701a0bAgent;

/**
 * Created by admin on 2017/5/23.
 */

public class MyApplication extends Application {

    @Override
    public	void	onCreate()	{

        super.onCreate();
        /**
         *  *	这⾥增加SDK的初始化代码
         *  */
        ag701a0bAgent.init(this);
    }
}
