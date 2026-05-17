package net.kdt.pojavlaunch.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.extra.ExtraCore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VersionListFragment extends Fragment {

    public static final String TAG = "VersionListFragment";
    private final List<String> mFinalVersionList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = requireContext().getResources().getIdentifier("fragment_version_list", "layout", requireContext().getPackageName());
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Header back navigation button click handler
        int backBtnId = requireContext().getResources().getIdentifier("btn_back_versions", "id", requireContext().getPackageName());
        View backButton = view.findViewById(backBtnId);
        if (backButton != null) {
            backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Target the layout's dynamic ListView component
        int listId = requireContext().getResources().getIdentifier("version_list_render", "id", requireContext().getPackageName());
        ListView listView = view.findViewById(listId);

        if (listView != null) {
            mFinalVersionList.clear();

            try {
                // SYSTEM FILE SCANNER: Directly inspects the official minecraft versions game folder
                File versionsDir = new File(Tools.DIR_GAME_NEW, "versions");
                if (versionsDir.exists() && versionsDir.isDirectory()) {
                    File[] files = versionsDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isDirectory()) {
                                // Dynamically extracts downloaded releases, mod-loaders, or snapshots
                                mFinalVersionList.add(file.getName());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Alpha-sort any found local folder configurations to keep custom setups grouped neatly
            Collections.sort(mFinalVersionList);

            // =================================================================
            // PREMIUM DEFAULT MANIFEST MAP (IF NO LOCAL VERSIONS ARE INSTALLED)
            // =================================================================
            if (mFinalVersionList.isEmpty()) {
                // Displays your exact custom sidebar fallback requirements out-of-the-box!
                mFinalVersionList.add("No installed versions");
                
                // 1. FABRIC MOD-LOADER ENGINES
                mFinalVersionList.add("Fabric 1.21.1");
                mFinalVersionList.add("Fabric 1.20.1");
                mFinalVersionList.add("Fabric 1.16.5");
                
                // 2. FORGE MOD-LOADER ENGINES
                mFinalVersionList.add("Forge 1.21.1");
                mFinalVersionList.add("Forge 1.20.1");
                mFinalVersionList.add("Forge 1.16.5");
                mFinalVersionList.add("Forge 1.12.2");
                
                // 3. OPTIFINE STANDALONE ENGINES
                mFinalVersionList.add("OptiFine 1.21");
                mFinalVersionList.add("OptiFine 1.20.4");
                mFinalVersionList.add("OptiFine 1.12.2");
                
                // 4. VANILLA RELEASES & SNAPSHOTS
                mFinalVersionList.add("1.21.1 (Latest Release)");
                mFinalVersionList.add("1.20.4");
                mFinalVersionList.add("Snapshot 24w14a");
                mFinalVersionList.add("1.8.9");
            }

            // Bind the scanned or default database array to your custom row layouts
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, mFinalVersionList) {
                @NonNull
                @Override
                public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                    View row = super.getView(position, convertView, parent);
                    TextView text = row.findViewById(android.R.id.text1);
                    
                    if (text != null) {
                        String name = mFinalVersionList.get(position);
                        text.setText(name);
                        text.setTextColor(android.graphics.Color.WHITE);
                        text.setTextSize(13f); // Strict float notation formatting to bypass compiler parsing rules
                        text.setPadding(32, 40, 32, 40);
                    }
                    
                    int drawableId = requireContext().getResources().getIdentifier("rounded_card_bg", "drawable", requireContext().getPackageName());
                    if (drawableId != 0) {
                        row.setBackgroundResource(drawableId);
                        row.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1C1D21")));
                    }
                    return row;
                }
            };

            listView.setAdapter(adapter);

            // Handle Selection Engine Clicks
            listView.setOnItemClickListener((parent1, view1, position, id) -> {
                String selectedVersion = mFinalVersionList.get(position);
                
                // Do nothing if the user accidentally taps the "No installed versions" banner line
                if (selectedVersion.equalsIgnoreCase("No installed versions")) {
                    return;
                }
                
                // Standardize custom descriptive string text formats into clean engine IDs
                if (selectedVersion.contains(" (")) {
                    selectedVersion = selectedVersion.split(" \\(")[0];
                } else if (selectedVersion.startsWith("Fabric ")) {
                    selectedVersion = selectedVersion.replace("Fabric ", "fabric-");
                } else if (selectedVersion.startsWith("Forge ")) {
                    selectedVersion = selectedVersion.replace("Forge ", "forge-");
                } else if (selectedVersion.startsWith("OptiFine ")) {
                    selectedVersion = selectedVersion.replace("OptiFine ", "optifine-");
                }

                try {
                    // Inject choices directly back into core configuration data contexts using universally compliant strings
                    ExtraCore.setValue("selected_version", selectedVersion);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                try {
                    // Force a core visual notification repaint to update the main menu spinner label text fields
                    ExtraCore.setValue("refresh_version", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
