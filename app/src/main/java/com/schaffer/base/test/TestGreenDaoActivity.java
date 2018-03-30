package com.schaffer.base.test;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import com.schaffer.base.R;
import com.schaffer.base.common.base.BaseEmptyActivity;
import com.schaffer.base.common.base.BasePresenter;
import com.schaffer.base.db.model.DaoMaster;
import com.schaffer.base.db.model.DaoSession;
import com.schaffer.base.db.model.GreenDaoUserDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class TestGreenDaoActivity extends BaseEmptyActivity<TestGreenDaoActivity, TestGreenDaoPresenter> {

    private GreenDaoUserDao mUserDao;

    @Override
    protected void inflateView() {
        setLeftClick(null);
        setActivityTitle("這是標題");
        inflateContent(R.layout.test_greendao);
    }

    @Override
    protected TestGreenDaoPresenter initPresenter() {
        return new TestGreenDaoPresenter();
    }

    @Override
    public boolean isShowTitleBar() {
        return true;
    }

    @Override
    protected void initData() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "user-db", null);
        SQLiteDatabase db = helper.getReadableDatabase();
        DaoMaster master = new DaoMaster(db);
        DaoSession session = master.newSession();
        mUserDao = session.getGreenDaoUserDao();

        mUserDao.rx();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert1();
            }
        });
    }

    @Override
    protected void refreshData() {

    }

    int times = 0;

    public GreenDaoUser create() {
        times++;
        GreenDaoUser user = new GreenDaoUser();
        user.setGender(times % 2);
        user.setAge(15 + times);
        user.setFirstName("firstName>>>" + times);
        user.setLastName("LastName>>>" + times);
        user.setId((long) (times + 1));

        return user;
    }

    public void insert1() {
        GreenDaoUser user = create();
        mPresenter.insertOrReplace(mUserDao, user, new BasePresenter.SimpleCurdSubscriber<GreenDaoUser>(mPresenter) {
            @Override
            public void onNext(GreenDaoUser greenDaoUser) {
                showLog(">>>" + greenDaoUser.toString());
            }
        });
    }

    public void delete(View v) {
        GreenDaoUser user = new GreenDaoUser();
        user.setGender(1);
        user.setAge(16);
        user.setFirstName("firstName>>>1");
        user.setLastName("LastName>>>1");
        user.setId(2L);
        mPresenter.deleteByKey(mUserDao, 2L, new BasePresenter.SimpleCurdSubscriber<Void>(mPresenter) {
            @Override
            public void onNext(Void aVoid) {
                showLog(">>>16岁的删除成功");
            }
        });
//        mPresenter.delete(mUserDao, user, new BasePresenter.SimpleCurdSubscriber<Void>(mPresenter) {
//            @Override
//            public void onNext(Void aVoid) {
//                showLog(">>>16岁的删除成功");
//            }
//        });
    }

    public void change(View v) {
        GreenDaoUser user = new GreenDaoUser();
        user.setGender(0);
        user.setAge(3030);
        user.setFirstName("firstName>>>2");
        user.setLastName("LastName>>>2");
        user.setId(3L);
        mPresenter.update(mUserDao, user, new BasePresenter.SimpleCurdSubscriber<GreenDaoUser>(mPresenter) {
            @Override
            public void onNext(GreenDaoUser user) {
                showLog("user>>>" + user.toString());
            }
        });
    }

    public void query(View v) {
        QueryBuilder<GreenDaoUser> builder = mUserDao.queryBuilder();

        builder.where(GreenDaoUserDao.Properties.FirstName.like("firstName%"));

//        List<GreenDaoUser> list = builder.list();
//        if (list != null) {
//            showLog("size>>>" + list.size());
//            for (GreenDaoUser user : list) {
//                showLog("user>>>" + user.toString());
//            }
//        }
        mPresenter.query(builder, new BasePresenter.MoreCurdsSubscriber<GreenDaoUser>(mPresenter) {

            @Override
            public void onNext(List<GreenDaoUser> data) {
                showLog("user>>>" + data.toString());
            }
        });
    }
}
