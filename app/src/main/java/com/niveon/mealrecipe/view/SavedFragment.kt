package com.niveon.mealrecipe.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.niveon.mealrecipe.R
import com.niveon.mealrecipe.adapter.SavedAdapter
import com.niveon.mealrecipe.databinding.FragmentSavedBinding
import com.niveon.mealrecipe.util.isVisible
import com.niveon.mealrecipe.util.setupRecyclerView
import com.niveon.mealrecipe.viewmodel.SavedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var favoriteAdapter: SavedAdapter

    private val viewModel: SavedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSavedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel.getSavedFoodList()
            viewModel.foodList.observe(viewLifecycleOwner) {
                if (it.data?.isEmpty() == true) {
                    savedEmptyLay.isVisible(true,savedList)
                    emptyLay.imgDisconnect.setAnimation(R.raw.empty)
                    emptyLay.imgDisconnect.playAnimation()
                }else{
                    savedEmptyLay.isVisible(false, savedList)
                    favoriteAdapter.setData(it.data!!)
                    savedList.setupRecyclerView(LinearLayoutManager(requireContext()), favoriteAdapter)
                    favoriteAdapter.setOnItemClickListener { food ->
                        val direction = SavedFragmentDirections.actionHomeToDetail(food.id)
                        findNavController().navigate(direction)
                    }
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}