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

import net.kdt.pojavlaunch.extra.ExtraCore;
import java.util.ArrayList;
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

            // =================================================================
            // MASTER DATABASE: ALL MOD-LOADERS, RELEASES, SNAPSHOTS, & PACKS
            // =================================================================
            
            // 1. FABRIC MOD-LOADER ENGINE OPTIONS
            mFinalVersionList.add("Fabric 1.21.1");
            mFinalVersionList.add("Fabric 1.20.4");
            mFinalVersionList.add("Fabric 1.20.1");
            mFinalVersionList.add("Fabric 1.19.2");
            mFinalVersionList.add("Fabric 1.18.2");
            mFinalVersionList.add("Fabric 1.16.5");

            // 2. FORGE MOD-LOADER ENGINE OPTIONS
            mFinalVersionList.add("Forge 1.21.1");
            mFinalVersionList.add("Forge 1.20.1");
            mFinalVersionList.add("Forge 1.19.2");
            mFinalVersionList.add("Forge 1.18.2");
            mFinalVersionList.add("Forge 1.16.5");
            mFinalVersionList.add("Forge 1.12.2");
            mFinalVersionList.add("Forge 1.8.9");
            mFinalVersionList.add("Forge 1.7.10");

            // 3. OPTIFINE STANDALONE ENGINE OPTIONS
            mFinalVersionList.add("OptiFine 1.21");
            mFinalVersionList.add("OptiFine 1.20.4");
            mFinalVersionList.add("OptiFine 1.20.1");
            mFinalVersionList.add("OptiFine 1.19.4");
            mFinalVersionList.add("OptiFine 1.16.5");
            mFinalVersionList.add("OptiFine 1.12.2");
            mFinalVersionList.add("OptiFine 1.8.9");

            // 4. POPULAR MODPACK RUNTIME SEMANTICS
            mFinalVersionList.add("Modpack: Cobblemon [Fabric]");
            mFinalVersionList.add("Modpack: Better Minecraft 1.20.1");
            mFinalVersionList.add("Modpack: RL Craft 1.12.2");
            mFinalVersionList.add("Modpack: Pixelmon Reforged");

            // 5. RECENT VANILLA RELEASES
            mFinalVersionList.add("1.21.1 (Latest Release)");
            mFinalVersionList.add("1.21");
            mFinalVersionList.add("1.20.6");
            mFinalVersionList.add("1.20.4");
            mFinalVersionList.add("1.20.2");
            mFinalVersionList.add("1.20.1");
            mFinalVersionList.add("1.19.4");
            mFinalVersionList.add("1.19.2");
            mFinalVersionList.add("1.18.2");
            mFinalVersionList.add("1.17.1");

            // 6. SNAPSHOTS & EXPERIMENTAL BUILDS
            mFinalVersionList.add("Snapshot 24w14a");
            mFinalVersionList.add("Snapshot 23w45a");
            mFinalVersionList.add("Snapshot 1.21-pre1");
            mFinalVersionList.add("Combat Test 8c");

            // 7. LEGACY & GOLDEN AGE CLASSICS
            mFinalVersionList.add("1.16.5");
            mFinalVersionList.add("1.12.2");
            mFinalVersionList.add("1.8.9");
            mFinalVersionList.add("1.7.10");
            mFinalVersionList.add("Alpha v1.2.6");
            mFinalVersionList.add("Beta 1.7.3");

            // Bind this entire combined master list to your custom layout list views
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
                
                // Parse and strip clean tags out if a user picks a tagged option
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
                    // Inject selection directly back into core configuration context values
                    ExtraCore.setValue("selected_version", selectedVersion);
                    ExtraCore.setValue("refresh_version", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                getParentFragmentManager().popBackStack();
            });
        }
    }
}
