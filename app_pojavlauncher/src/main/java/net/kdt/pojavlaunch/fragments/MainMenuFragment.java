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

        // =================================================================
        // 1. TOTAL BANISHMENT OF THE OLD YELLOW LINE, TEXTS, AND HEADERS
        // =================================================================
        try {
            // Target the root display view frame context of the active running application window
            View rootDecorView = requireActivity().getWindow().getDecorView();
            
            // Comprehensive array targeting all old header views, lines, and account layout widgets
            String[] stubbornViewsList = {
                "main_header_layout", 
                "top_bar", 
                "account_header", 
                "add_account_layout", 
                "add_account_text", 
                "yellow_line_separator"
            };
            
            // Loop through each element ID string, resolve its resource pointer, and turn visibility off completely
            for (String targetElementId : stubbornViewsList) {
                int resolvedResourceId = requireContext().getResources().getIdentifier(
                    targetElementId, 
                    "id", 
                    requireContext().getPackageName()
                );
                
                if (resolvedResourceId != 0) {
                    View stubbornViewInstance = rootDecorView.findViewById(resolvedResourceId);
                    if (stubbornViewInstance != null) {
                        stubbornViewInstance.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // =================================================================
        // 2. CORE COMPONENT INITIALIZATION & ACTION ROUTING
        // =================================================================
        Button mPlayButton = view.findViewById(R.id.play_button);
        mVersionSpinner = view.findViewById(R.id.mc_version_spinner);
        
        // Target your top right layout gear settings wheel view button context reference
        View mGearSettingsButton = view.findViewById(R.id.edit_profile_button);

        // Core Minecraft Launch Thread Bind
        if (mPlayButton != null) {
            mPlayButton.setOnClickListener(v -> ExtraCore.setValue(ExtraConstants.LAUNCH_GAME, true));
        }

        // Tapping the version dropdown container switches directly to our premium version screen fragment
        if (mVersionSpinner != null) {
            mVersionSpinner.setOnClickListener(v -> triggerVersionFragmentTransaction());
        }

        // REDIRECTION FIX: Tapping the top right gear button now safely routes into our clean fragment layout!
        if (mGearSettingsButton != null) {
            mGearSettingsButton.setOnClickListener(v -> triggerVersionFragmentTransaction());
        }

        // Direct account profile dashboard block interaction map routing link
        int accountBoxId = requireContext().getResources().getIdentifier("account_center_block", "id", requireContext().getPackageName());
        View accountDock = view.findViewById(accountBoxId);
        if (accountDock != null) {
            accountDock.setOnClickListener(v -> {
                // Completely bypasses the old accordion editor page window popup context frames!
                triggerVersionFragmentTransaction();
            });
        }
    }

    /** Helper routing method to execute clean fragment transitions safely across your touch areas */
    private void triggerVersionFragmentTransaction() {
        try {
            int mainLayoutContainerId = requireContext().getResources().getIdentifier(
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
                .replace(mainLayoutContainerId, new VersionListFragment())
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
