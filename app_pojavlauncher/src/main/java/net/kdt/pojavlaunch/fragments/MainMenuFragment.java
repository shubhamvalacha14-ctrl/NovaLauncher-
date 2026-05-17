package net.kdt.pojavlaunch.fragments;

import static net.kdt.pojavlaunch.Tools.openPath;
import static net.kdt.pojavlaunch.Tools.shareLog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
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

        // =================================================================
        // 1. CLEAN CONTENT ARCHITECTURE RE-ALIGNMENT
        // =================================================================
        try {
            if (view != null) {
                view.setFitsSystemWindows(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // =================================================================
        // 2. CORE COMPONENT INITIALIZATION & INTERACTION ROUTING
        // =================================================================
        Button mPlayButton = view.findViewById(R.id.play_button);
        mVersionSpinner = view.findViewById(R.id.mc_version_spinner);
        View mGearSettingsButton = view.findViewById(R.id.edit_profile_button);

        // Core Game Launch Execution Engine Hook
        if (mPlayButton != null) {
            mPlayButton.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));
        }

        // Tapping the custom version select panel goes straight to your full-screen menu fragment
        if (mVersionSpinner != null) {
            mVersionSpinner.setOnClickListener(v -> triggerVersionFragmentTransaction());
        }

        // Tapping the layout gear settings button also routes into our clean fragment list!
        if (mGearSettingsButton != null) {
            mGearSettingsButton.setOnClickListener(v -> triggerVersionFragmentTransaction());
        }

        // Tapping the secondary layout version block routes into the version selector sheet layout frame
        int accountBoxId = requireContext().getResources().getIdentifier("account_center_block", "id", requireContext().getPackageName());
        View accountDock = view.findViewById(accountBoxId);
        if (accountDock != null) {
            accountDock.setOnClickListener(v -> triggerVersionFragmentTransaction());
        }

        // =================================================================
        // 3. SIDEBAR "ADD ACCOUNT" LINK OPERATION FUNCTIONAL INTERFACE
        // =================================================================
        try {
            // Target your customized sidebar add account click block element card dynamically
            int sidebarAddAccountId = requireContext().getResources().getIdentifier("add_account", "id", requireContext().getPackageName());
            View sidebarAddAccountBtn = view.findViewById(sidebarAddAccountId);
            
            if (sidebarAddAccountBtn == null) {
                // Secondary backup check for button layout target names
                int altAddAccountId = requireContext().getResources().getIdentifier("add_account_card", "id", requireContext().getPackageName());
                sidebarAddAccountBtn = view.findViewById(altAddAccountId);
            }

            if (sidebarAddAccountBtn != null && mVersionSpinner != null) {
                sidebarAddAccountBtn.setOnClickListener(v -> {
                    // Instantly fires up Pojav's official, native account profile editor login wizard!
                    mVersionSpinner.openProfileEditor(requireActivity());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Fragment transaction transaction manager helper script to swap launcher sheets cleanly */
    private void triggerVersionFragmentTransaction() {
        try {
            int mainContainerId = requireContext().getResources().getIdentifier(
                "fragment_menu_main", 
                "id", 
                requireContext().getPackageName()
            );
            
            getParentFragmentManager().beginTransaction()
                .setCustomAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .replace(mainContainerId, new VersionListFragment())
                .addToBackStack(null)
                .commit();
        } catch (Exception e) {
            e.printStackTrace();
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
