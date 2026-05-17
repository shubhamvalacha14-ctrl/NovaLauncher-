package net.kdt.pojavlaunch.fragments;

import static net.kdt.pojavlaunch.Tools.openPath;
import static net.kdt.pojavlaunch.Tools.shareLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kdt.mcgui.mcVersionSpinner;

import net.kdt.pojavlaunch.CustomControlsActivity;
import git.artdeell.mojo.R;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.contracts.OpenDocumentWithExtension;
import net.kdt.pojavlaunch.extra.ExtraConstants;
import net.kdt.pojavlaunch.extra.ExtraCore;
import net.kdt.pojavlaunch.instances.Instance;
import net.kdt.pojavlaunch.instances.Instances;
import net.kdt.pojavlaunch.progresskeeper.ProgressKeeper;
import net.kdt.pojavlaunch.utils.FileUtils;

import java.io.File;

public class MainMenuFragment extends Fragment {
    public static final String TAG = "MainMenuFragment";

    private mcVersionSpinner mVersionSpinner;

    private final ActivityResultLauncher<Object> mModInstallerLauncher =
            registerForActivityResult(new OpenDocumentWithExtension("jar"), (data)->{
                if(data != null) Tools.launchModInstaller(requireContext(), data);
            });

    public MainMenuFragment(){
        super(R.layout.fragment_launcher);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ==========================================
        // 1. RUNTIME TOP HEADER REMOVAL (CRITICAL FIX)
        // ==========================================
        try {
            // Hunts down and destroys the stubborn top navigation action bar overlay container at runtime
            int topHeaderId = requireContext().getResources().getIdentifier("main_header_layout", "id", requireContext().getPackageName());
            View topHeader = requireActivity().findViewById(topHeaderId);
            if (topHeader != null) {
                topHeader.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ==========================================
        // 2. VIEW BINDINGS & CLICK ROUTERS
        // ==========================================
        Button mPlayButton = view.findViewById(R.id.play_button);
        mVersionSpinner = view.findViewById(R.id.mc_version_spinner);

        // Core Game Engine Launch Execution
        if (mPlayButton != null) {
            mPlayButton.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));
        }

        // Dynamic Full-Screen Version List Sheet Transition Override
        if (mVersionSpinner != null) {
            mVersionSpinner.setOnClickListener(v -> {
                getParentFragmentManager().beginTransaction()
                    .setCustomAnimations(
                        android.R.anim.fade_in,
                        android.R.anim.fade_out,
                        android.R.anim.fade_in,
                        android.R.anim.fade_out
                    )
                    .replace(requireContext().getResources().getIdentifier("fragment_menu_main", "id", requireContext().getPackageName()), new VersionListFragment())
                    .addToBackStack(null)
                    .commit();
            });
        }

        // Clean Account Card Center Dashboard Execution
        int accountBoxId = requireContext().getResources().getIdentifier("account_center_block", "id", requireContext().getPackageName());
        View accountDock = view.findViewById(accountBoxId);
        if (accountDock != null && mVersionSpinner != null) {
            accountDock.setOnClickListener(v -> {
                // Instantly requests the core engine to show the native account profiles selector layout modal
                mVersionSpinner.openProfileEditor(requireActivity());
            });
        }
    }

    private void openGameDirectory(Context context) {
        Instance instance = Instances.loadSelectedInstance();
        if(instance == null) {
            Toast.makeText(context, R.string.no_instance, Toast.LENGTH_LONG).show();
            return;
        }
        File gameDirectory = instance.getGameDirectory();
        if(FileUtils.ensureDirectorySilently(gameDirectory)) {
            openPath(context, gameDirectory, false);
        } else {
            Toast.makeText(context, R.string.gamedir_open_failed, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ExtraCore.setValue(ExtraConstants.REFRESH_ACCOUNT_SPINNER, true);
    }

    private void runInstallerWithConfirmation() {
        if (ProgressKeeper.getTaskCount() == 0) {
            mModInstallerLauncher.launch(null);
        } else {
            Toast.makeText(requireContext(), R.string.tasks_ongoing, Toast.LENGTH_LONG).show();
        }
    }
}
