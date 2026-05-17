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

        // Header back navigation button click router
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
                // SYSTEM SCANNER ENGINE: Directly opens your launcher game directories storage map
                File versionsDir = new File(Tools.DIR_GAME_NEW, "versions");
                if (versionsDir.exists() && versionsDir.isDirectory()) {
                    File[] files = versionsDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isDirectory()) {
                                // Dynamically maps Fabric, Forge, OptiFine, Modpacks, Snapshots, and Vanilla profiles
                                mFinalVersionList.add(file.getName());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Alphabetically sort the instances list to keep mod engines organized
            Collections.sort(mFinalVersionList);

            // Safety failback items display if local directory lists are empty
            if (mFinalVersionList.isEmpty()) {
                mFinalVersionList.add("No versions found! Download Fabric/Forge or Vanilla first.");
            }

            // Bind the full folder scan data array to the stylized row layout context template
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
                        text.setTextSize(13f); // Strict float format notation to pass compiler check
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
                    // Saves user selection straight into global runtime configurations using raw strings
                    ExtraCore.setValue("selected_version", selectedVersion);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                try {
                    // Sends global refresh notification signals to repaint structural home screen widgets
                    ExtraCore.setValue("refresh_version", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
