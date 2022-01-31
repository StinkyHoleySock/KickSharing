package com.example.criminalintent_

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "RequestListFragment"

class RequestListFragment: Fragment() {

    interface Callbacks {
        fun onRequestSelected(requestId: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var requestRecyclerView: RecyclerView
    private var adapter: RequestAdapter? = RequestAdapter(emptyList())

    private val requestListViewModel: RequestListViewModel by lazy{
        ViewModelProviders.of(this).get(RequestListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request_list, container, false)

        requestRecyclerView = view.findViewById(R.id.request_recycler_view) as RecyclerView
        requestRecyclerView.layoutManager = LinearLayoutManager(context)

        requestRecyclerView.adapter = adapter

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestListViewModel.requestListLiveData.observe(
            viewLifecycleOwner,
            Observer { requests ->
                requests?.let {
                    Log.i(TAG, "Got requests ${requests.size}")
                    updateUI(requests)
                }
            }
        )
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_request_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_request -> {
                val request = Request()
                requestListViewModel.addRequest(request)
                callbacks?.onRequestSelected(request.id)
                true
            } else -> return super.onOptionsItemSelected(item)
        }
    }

    private inner class RequestHolder(view: View): RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var request: Request

        val titleTextView: TextView = itemView.findViewById(R.id.request_title)
        val dateTextView: TextView = itemView.findViewById(R.id.request_date)

        private val solvedImageView: ImageView = itemView.findViewById(R.id.request_solved)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(request: Request) {
            this.request = request
            titleTextView.text = this.request.title
    //        moduleTextView.text = this.request.module
            dateTextView.text = this.request.currentDate.toString()
    //        planDateTextView.text = this.request.planDate.toString()
    //        factDateTextView.text = this.request.factDate.toString()

            solvedImageView.visibility = if (request.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }

        }

        override fun onClick(v: View?) {
            callbacks?.onRequestSelected(request.id)
        }
    }

    private inner class RequestAdapter(var requests: List<Request>): RecyclerView.Adapter<RequestHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestHolder {
            val view = layoutInflater.inflate(R.layout.list_item_request, parent, false)
            return RequestHolder(view)
        }

        override fun onBindViewHolder(holder: RequestHolder, position: Int) {
            val request = requests[position]
            holder.bind(request)
        }

        override fun getItemCount() = requests.size

    }

    private fun updateUI(requests: List<Request>){
        adapter = RequestAdapter(requests)
        requestRecyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(): RequestListFragment {
            return RequestListFragment()
        }
    }
}