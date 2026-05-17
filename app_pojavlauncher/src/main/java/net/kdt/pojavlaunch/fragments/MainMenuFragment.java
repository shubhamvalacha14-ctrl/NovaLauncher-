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
        // 1. WINDOW TREE SWEEP: FORCE-HIDE OLD TOP-BAR & YELLOW LINE
        // =================================================================
        try {
            // Access the running activity's master display layer layout
            ViewGroup rootLayout = (ViewGroup) requireActivity().getWindow().getDecorView().getRootView();
            
            // Loop through structural container elements to kill header views bleeding into fragments
            for (int i = 0; i < rootLayout.getChildCount(); i++) {
                View child = rootLayout.getChildAt(i);
                if (child != null && (child.getClass().getName().contains("ConstraintLayout") || child.getClass().getName().contains("RelativeLayout"))) {
                    int checkHeaderId = requireContext().getResources().getIdentifier("main_header_layout", "id", requireContext().getPackageName());
                    View innerHeader = child.findViewById(checkHeaderId);
                    if (innerHeader != null) {
                        innerHeader.setVisibility(View.GONE);
                    }
                }
            }

            // Fallback direct pointer wipe to wipe the old text elements and line bars cleanly
            String[] stubbornViews = {
                "main_header_layout", 
                "top_bar", 
                "account_header", 
                "add_account_layout", 
                "add_account_text", 
                "yellow_line_separator"
            };
            for (String targetId : stubbornViews) {
                int resId = requireContext().getResources().getIdentifier(targetId, "id", requireContext().getPackageName());
                View targetView = requireActivity().findViewById(resId);
                if (targetView != null) {
                    targetView.setVisibility(View.GONE);
                }
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

        // Game Launch Engine Hook
        if (mPlayButton != null) {
            mPlayButton.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));
        }

        // Tapping the version dropdown box goes straight to our custom full-screen list fragment
        if (mVersionSpinner != null) {
            mVersionSpinner.setOnClickListener(v -> triggerVersionFragmentTransaction());
        }

        // Tapping the top right gear settings button now also safely routes into our clean fragment list!
        if (mGearSettingsButton != null) {
            mGearSettingsButton.setOnClickListener(v -> triggerVersionFragmentTransaction());
        }

        // Tapping the main version sidebar card box routes into the version selector sheet layout frame
        int accountBoxId = requireContext().getResources().getIdentifier("account_center_block", "id", requireContext().getPackageName());
        View accountDock = view.findViewById(accountBoxId);
        if (accountDock != null) {
            accountDock.setOnClickListener(v -> triggerVersionFragmentTransaction());
        }

        // =================================================================
        // 3. NEW SIDEBAR "ADD ACCOUNT" ACTION FUNCTIONAL INTERFACE
        // =================================================================
        try {
            // Dynamically hooks your new sidebar layout "Add Account" selection button
            int sidebarAddAccountId = requireContext().getResources().getIdentifier("add_account_card", "id", requireContext().getPackageName());
            View sidebarAddAccountBtn = view.findViewById(sidebarAddAccountId);
            
            if (sidebarAddAccountBtn == null) {
                // Secondary check for text-layer click variants
                int altAddAccountId = requireContext().getResources().getIdentifier("add_account", "id", requireContext().getPackageName());
                sidebarAddAccountBtn = view.findViewById(altAddAccountId);
            }

            if (sidebarAddAccountBtn != null && mVersionSpinner != null) {
                sidebarAddAccountBtn.setOnClickListener(v -> {
                    // Triggers Pojav's official, native login prompt manager window seamlessly
                    mVersionSpinner.openProfileEditor(requireActivity());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Fragment transaction helper routing task to swap layout sheets cleanly */
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
