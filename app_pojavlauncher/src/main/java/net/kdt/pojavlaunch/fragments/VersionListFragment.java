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
import net.kdt.pojavlaunch.instances.Instance;
import net.kdt.pojavlaunch.instances.Instances;

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

        // Header back navigation button
        int backBtnId = requireContext().getResources().getIdentifier("btn_back_versions", "id", requireContext().getPackageName());
        View backButton = view.findViewById(backBtnId);
        if (backButton != null) {
            backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

        // Target the layout's dynamic ListView element
        int listId = requireContext().getResources().getIdentifier("version_list_render", "id", requireContext().getPackageName());
        ListView listView = view.findViewById(listId);

        if (listView != null) {
            mFinalVersionList.clear();

            try {
                // POJAV NATIVE ENGINE HOOK: Scan files directly inside the official minecraft versions game folder
                File versionsDir = new File(Tools.DIR_GAME_NEW, "versions");
                if (versionsDir.exists() && versionsDir.isDirectory()) {
                    File[] files = versionsDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isDirectory()) {
                                // This extracts everything: Fabric, Forge, OptiFine, Snapshots, Custom Releases
                                mFinalVersionList.add(file.getName());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Alpha-sort versions so newer/modded setups group cleanly
            Collections.sort(mFinalVersionList);

            // True Fallback if the folder scan returned zero results
            if (mFinalVersionList.isEmpty()) {
                mFinalVersionList.add("No versions found! Tap 'Install New' in settings.");
            }

            // Bind the scanned live folder data straight to your stylized row elements
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
                        text.setTextSize(13f);
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
                
                try {
                    // Update PojavLauncher's underlying runtime selection instance map safely
                    Instance customInstance = new Instance(selectedVersion);
                    Instances.selectInstance(customInstance);
                } catch (Exception e) {
                    // Alternative standard method backup router if Instance requires absolute context parameters
                    try {
                        net.kdt.pojavlaunch.extra.ExtraCore.setValue("selected_version", selectedVersion);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
