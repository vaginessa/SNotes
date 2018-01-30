package in.snotes.snotes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddNotesActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_notes_add)
    Toolbar toolbarNotesAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        ButterKnife.bind(this);


        setSupportActionBar(toolbarNotesAdd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_add_notes, new AddNotesFragment(), "add-notes-fragment")
                    .commit();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            AddNotesFragment addNotesFragment = (AddNotesFragment) getSupportFragmentManager().findFragmentByTag("add-notes-fragment");
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
