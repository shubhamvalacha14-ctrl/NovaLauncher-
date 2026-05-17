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

import java.lang.reflect.Method;
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

            try {
                // INTERNAL REFLECTION ENGINE: GRAB POJAV'S ENTIRE BUILT-IN VERSION MANIFEST
                Class<?> versionUtilsClass = Class.forName("net.kdt.pojavlaunch.utils.VersionUtils");
                Method getVersionsMethod = versionUtilsClass.getMethod("getDownloadableVersions");
                
                // This pulls the entire default manifest: All Vanilla Releases, Snapshots, and Alphas
                List<?> officialVersions = (List<?>) getVersionsMethod.invoke(null);
                
                if (officialVersions != null) {
                    for (Object versionObj : officialVersions) {
                        mFinalVersionList.add(versionObj.toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // FALLBACK SYSTEM: If manifestation fetch fails, populate standard release manifests dynamically
            if (mFinalVersionList.isEmpty()) {
                mFinalVersionList.add("1.21.1 (Latest Release)");
                mFinalVersionList.add("1.21");
                mFinalVersionList.add("1.20.6");
                mFinalVersionList.add("1.20.4");
                mFinalVersionList.add("1.20.1");
                mFinalVersionList.add("1.19.4");
                mFinalVersionList.add("1.18.2");
                mFinalVersionList.add("1.17.1");
                mFinalVersionList.add("1.16.5");
                mFinalVersionList.add("1.12.2");
                mFinalVersionList.add("24w14a (Snapshot)");
            }

            // Bind the massive manifest list array to your stylized layout template rows
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
                
                // Strip description tags text if present to save pure version id string strings
                if (selectedVersion.contains(" ")) {
                    selectedVersion = selectedVersion.split(" ")[0];
                }

                try {
                    // Saves the selection straight to core configuration engine context registers
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
