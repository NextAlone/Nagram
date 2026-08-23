package tw.nekomimi.nekogram.settings;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import xyz.nextalone.nagram.NaConfig;

/** Every original Nagram settings screen, flattened into one continuous list. */
public class UltraToggleSettingsActivity extends BaseNekoXSettingsActivity {
    private static final int TYPE_ULTRA_TOGGLE = -101;
    private static final int TYPE_SECTION_HEADER = -102;
    private static final int TYPE_SECTION_DIVIDER = -103;

    private final List<BaseNekoXSettingsActivity> sections = Arrays.asList(
            new NekoGeneralSettingsActivity(),
            new NekoAccountSettingsActivity(),
            new NekoChatSettingsActivity(),
            new NekoExperimentalSettingsActivity(),
            new NekoDebugSettingsActivity()
    );
    private final ArrayList<Entry> entries = new ArrayList<>();

    public UltraToggleSettingsActivity() {
        cellGroup = new tw.nekomimi.nekogram.config.CellGroup(this);
    }

    @Override
    public String getTitle() {
        return LocaleController.getString(R.string.NekoSettings);
    }

    @Override
    public View createView(Context context) {
        View result = super.createView(context);
        entries.clear();
        entries.add(Entry.ultraToggle());

        for (int sectionIndex = 0; sectionIndex < sections.size(); sectionIndex++) {
            BaseNekoXSettingsActivity section = sections.get(sectionIndex);
            section.setParentLayout(parentLayout);
            section.onFragmentCreate();
            section.createView(context);

            entries.add(Entry.divider());
            entries.add(Entry.header(section.getTitle()));
            for (int position = 0; position < section.listAdapter.getItemCount(); position++) {
                entries.add(Entry.source(sectionIndex, position));
            }
        }
        entries.add(Entry.divider());

        listAdapter = new CompositeAdapter();
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position, x, y) -> {
            Entry entry = entries.get(position);
            if (entry.kind == Entry.ULTRA) {
                boolean enabled = NaConfig.INSTANCE.getUltraToggle().toggleConfigBool();
                ((TextCheckCell) view).setChecked(enabled);
                if (!enabled) finishFragment();
            } else if (entry.kind == Entry.SOURCE) {
                BaseNekoXSettingsActivity source = sections.get(entry.section);
                source.listView.clickItem(view, entry.position);
            }
        });
        return result;
    }

    @Override
    public void onFragmentDestroy() {
        for (BaseNekoXSettingsActivity section : sections) {
            section.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    private final class CompositeAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public int getItemCount() {
            return entries.size();
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            Entry entry = entries.get(holder.getAdapterPosition());
            if (entry.kind == Entry.ULTRA) return true;
            if (entry.kind != Entry.SOURCE) return false;
            return sections.get(entry.section).cellGroup.rows.get(entry.position).isEnabled();
        }

        @Override
        public int getItemViewType(int position) {
            Entry entry = entries.get(position);
            if (entry.kind == Entry.ULTRA) return TYPE_ULTRA_TOGGLE;
            if (entry.kind == Entry.HEADER) return TYPE_SECTION_HEADER;
            if (entry.kind == Entry.DIVIDER) return TYPE_SECTION_DIVIDER;
            int sourceType = sections.get(entry.section).listAdapter.getItemViewType(entry.position);
            return (entry.section + 1) * 1000 + sourceType;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_ULTRA_TOGGLE) {
                view = new TextCheckCell(parent.getContext());
            } else if (viewType == TYPE_SECTION_HEADER) {
                view = new HeaderCell(parent.getContext());
            } else if (viewType == TYPE_SECTION_DIVIDER) {
                view = new ShadowSectionCell(parent.getContext());
            } else {
                int section = viewType / 1000 - 1;
                int sourceType = viewType % 1000;
                return sections.get(section).listAdapter.onCreateViewHolder(parent, sourceType);
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Entry entry = entries.get(position);
            if (entry.kind == Entry.ULTRA) {
                ((TextCheckCell) holder.itemView).setTextAndCheck(
                        LocaleController.getString(R.string.UltraToggle),
                        NaConfig.INSTANCE.getUltraToggle().Bool(), false);
            } else if (entry.kind == Entry.HEADER) {
                ((HeaderCell) holder.itemView).setText(entry.title);
            } else if (entry.kind == Entry.DIVIDER) {
                ((ShadowSectionCell) holder.itemView).setTopBottom(true, true);
            } else {
                sections.get(entry.section).listAdapter.onBindViewHolder(holder, entry.position);
            }
        }
    }

    private static final class Entry {
        static final int ULTRA = 0;
        static final int HEADER = 1;
        static final int DIVIDER = 2;
        static final int SOURCE = 3;
        final int kind;
        final int section;
        final int position;
        final String title;

        private Entry(int kind, int section, int position, String title) {
            this.kind = kind;
            this.section = section;
            this.position = position;
            this.title = title;
        }

        static Entry ultraToggle() { return new Entry(ULTRA, -1, -1, null); }
        static Entry header(String title) { return new Entry(HEADER, -1, -1, title); }
        static Entry divider() { return new Entry(DIVIDER, -1, -1, null); }
        static Entry source(int section, int position) { return new Entry(SOURCE, section, position, null); }
    }
}
