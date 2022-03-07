package net.veldor.oblepiha_kotlin.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.ActivityMessagesBinding
import net.veldor.oblepiha_kotlin.model.adapters.MessagesAdapter
import net.veldor.oblepiha_kotlin.model.data_source.GetMessagesListDataSource
import net.veldor.oblepiha_kotlin.model.data_source.MessagesListItemDiffCallback
import net.veldor.oblepiha_kotlin.model.delegates.MessageDelegate
import net.veldor.oblepiha_kotlin.model.selections.MessageItem
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import net.veldor.oblepiha_kotlin.model.view_models.MessageViewModel
import net.veldor.oblepiha_kotlin.view.fragments.AccrualsPowerFragment
import java.util.concurrent.Executors


class MessagesActivity : AppCompatActivity(), MessageDelegate {
    private lateinit var dataSource: GetMessagesListDataSource
    private lateinit var viewModel: MessageViewModel
    private lateinit var binding: ActivityMessagesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        viewModel =
            ViewModelProvider(this).get(MessageViewModel::class.java)
        binding = ActivityMessagesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleRecycler()

        binding.swipeLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        binding.swipeLayout.setOnRefreshListener {
            handleRecycler()
        }

        // открою целевое уведомление, если оно выбрано
        val actionType = intent.getStringExtra("type")
        Log.d("surprise", "MessagesActivity.kt 50 onCreate action type is $actionType")
        if (actionType != null) {
            if (actionType == "broadcast") {
                val broadcastId = intent.getStringExtra("id")
                if (broadcastId != null) {
                    viewModel.markBroadcastAsRead(broadcastId)
                    AlertDialog.Builder(this)
                        .setTitle(intent.getStringExtra("title"))
                        .setMessage(intent.getStringExtra("text"))
                        .setNegativeButton(getString(R.string.close_title), null)
                        .setPositiveButton(getString(R.string.delete_message)) { _, _ ->
                            viewModel.deleteBroadcast(broadcastId)
                        }
                        .show()
                    (binding.recyclerView.adapter as MessagesAdapter).markBroadcastAsRead(
                        broadcastId
                    )
                }
            } else if (actionType == "new_bill" || actionType == "transaction_confirm") {
                val id = intent.getStringExtra("id")
                if (id != null) {
                    viewModel.markAsRead(id)
                    AlertDialog.Builder(this)
                        .setTitle(intent.getStringExtra("title"))
                        .setMessage(intent.getStringExtra("text"))
                        .setNegativeButton(getString(R.string.close_title), null)
                        .setPositiveButton(getString(R.string.delete_message)) { _, _ ->
                            viewModel.delete(id)
                        }
                        .setNeutralButton(getString(R.string.show_bill_info_message)) { _, _ ->
                            val startIntent = Intent(this, ContentActivity::class.java)
                            startIntent.putExtra("bill_id", intent.getStringExtra("billId"))
                            startActivity(startIntent)
                        }
                        .show()
                    (binding.recyclerView.adapter as MessagesAdapter).markAsRead(id.toInt())
                }
            }
        }

        setupObservers()
    }

    private fun setupObservers() {

        App.instance.connectionError.observe(this) {
            if(it != null && it){
                binding.swipeLayout.isRefreshing = false

                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.no_connection_title),
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.setAction(getString(R.string.retry_title)) {
                    // reload fragment in nav
                    handleRecycler()
                }
                snackbar.show()
            }
        }

        MessageViewModel.liveDataReceived.observe(this) {
            binding.swipeLayout.isRefreshing = false
            if (it > 0) {
                listIsNotEmpty()
            }
            else{
                listIsEmpty()
            }
        }
        MessageViewModel.liveRequireUpdateList.observe(this) {
            if (it) {
                binding.swipeLayout.isRefreshing = true
                handleRecycler()
            }
        }
    }

    private fun handleRecycler() {
        // DataSource
        dataSource = GetMessagesListDataSource()
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
        val pageList: PagedList<MessageItem?> = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(AccrualsPowerFragment.MainThreadExecutor())
            .build()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MessagesAdapter(MessagesListItemDiffCallback(), this)
        adapter.submitList(pageList)
        binding.recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.messages_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                startActivity(Intent(this, ContentActivity::class.java))
                finish()
                return true
            }
            R.id.action_mark_all_read -> {
                viewModel.markAllRead()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ContentActivity::class.java))
        finish()
    }

    override fun messageClicked(item: MessageItem?) {
        if (item != null) {
            viewModel.markAsRead(item)
            (binding.recyclerView.adapter as MessagesAdapter).markAsRead(item)
            // покажу диалог с подробностями
            val builder = AlertDialog.Builder(this)
                .setTitle(item.title)
                .setMessage(item.message)
                .setNegativeButton(getString(R.string.close_title), null)
                .setPositiveButton(getString(R.string.delete_message)) { _, _ ->
                    viewModel.delete(item)
                    binding.swipeLayout.isRefreshing = true
                    handleRecycler()
                }
            if (item.type == "new_bill" || item.type == "pay_confirm") {
                val billNum = GrammarHandler.getBillNumberFromString(item.message);
                Log.d("surprise", "MessagesActivity.kt 166 messageClicked $billNum")
                if (billNum != null) {
                    builder.setNeutralButton(getString(R.string.show_bill_info_message)) { _, _ ->
                        val startIntent = Intent(this, ContentActivity::class.java)
                        startIntent.putExtra("bill_id", billNum)
                        startActivity(startIntent)
                    }
                }
            } else if (item.type == "power_use") {
                builder.setNeutralButton(getString(R.string.show_power_use)) { _, _ ->
                    val startIntent = Intent(this, ContentActivity::class.java)
                    startIntent.putExtra("type", "power_data")
                    startActivity(startIntent)
                }
            }
            builder.show()
        }
    }

    override fun listIsEmpty() {
        binding.noMessagesText.visibility = View.VISIBLE
    }

    override fun listIsNotEmpty() {
        binding.noMessagesText.visibility = View.GONE
    }
}