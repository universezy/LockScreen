package lockscreen.zengyu.com.lockscreen;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends Activity {
    private final int PERMISSION_REQUEST_CODE = 0;
    private ComponentName componentName;
    private DevicePolicyManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void init() {
        componentName = new ComponentName(this, LockReceiver.class);
        manager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
    }

    private void checkPermission() {
        if (manager != null) {
            if (!manager.isAdminActive(componentName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            } else {
                lock();
            }
        } else {
            Toast.makeText(this, R.string.toast_problem, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            lock();
        } else {
            finish();
        }
    }

    private void lock() {
        if (manager != null) {
            manager.lockNow();
            finish();
        } else {
            Toast.makeText(this, R.string.toast_problem, Toast.LENGTH_SHORT).show();
        }
    }
}
