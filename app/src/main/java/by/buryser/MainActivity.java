package by.buryser;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

public class MainActivity extends ListActivity {
    private static final int EDIT_USER = 1;
    private static final int ADD_USER = 2;

    private DBAdapter dbAdapter;
    private ArrayAdapter<User> userArrayAdapter;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbAdapter = new DBAdapter(this).open();
        //init if empty
        if (dbAdapter.getAllUsers().size() == 0) {
            initializeDB();
        }
        //set data list
        updateUserList();
        //register for context menu
        ListView listView = getListView();
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                CharSequence text;
                User user = (User) parent.getItemAtPosition(position);
                text = "id = " + user.getId();
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        User user = userArrayAdapter.getItem(info.position);
        if (user != null) {
            menu.setHeaderTitle(user.getName());
        }
        currentUser = user;

        menu.add(Menu.NONE, v.getId(), Menu.NONE, "Edit");
        menu.add(Menu.NONE, v.getId(), Menu.NONE, "Delete");
        menu.add(Menu.NONE, v.getId(), Menu.NONE, "Add new User");
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getTitle().toString()){
            case "Edit":
                intent = new Intent("by.buryser.lab3.EDIT_USER");
                intent.putExtra("name", currentUser.getName());
                startActivityForResult(intent, EDIT_USER);
                break;
            case "Delete":
                dbAdapter.removeUser(currentUser.getId());
                updateUserList();
                Toast.makeText(getApplicationContext(),
                        currentUser.getName() + " deleted!", Toast.LENGTH_SHORT).show();
                break;
            case "Add new User":
                intent = new Intent("by.buryser.lab3.EDIT_USER");
                startActivityForResult(intent, ADD_USER);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (requestCode == EDIT_USER) {
            String name = data.getStringExtra("name");
            dbAdapter.updateUser(currentUser.getId(), new User(name));
            Toast.makeText(getApplicationContext(),
                    currentUser.getName() + " renamed to " + name, Toast.LENGTH_SHORT).show();
        }
        if (requestCode == ADD_USER) {
            String name = data.getStringExtra("name");
            dbAdapter.insertUser(new User(name));
            Toast.makeText(getApplicationContext(),
                    name + " added!", Toast.LENGTH_SHORT).show();
        }
        updateUserList();
    }

    private void updateUserList() {
        userArrayAdapter = new ArrayAdapter<User>(this, R.layout.list_item, dbAdapter.getAllUsers());
        setListAdapter(userArrayAdapter);
    }

    private void initializeDB(){
        dbAdapter.insertUser(new User("Nina"));
        dbAdapter.insertUser(new User("Adam"));
        dbAdapter.insertUser(new User("Kyle"));
        dbAdapter.insertUser(new User("Stan"));
        dbAdapter.insertUser(new User("Arnold"));
        dbAdapter.insertUser(new User("Richie"));
    }
}
