package lockscreen.zengyu.com.lockscreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
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
        try {
            super.onDestroy();
        } finally {
            Process.killProcess(Process.myPid());
        }
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
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.policy_content));
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
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                lock();
            } else if (resultCode == RESULT_CANCELED) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setMessage(R.string.dialog_content).setCancelable(false)
                        .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .create().show();
            }
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
