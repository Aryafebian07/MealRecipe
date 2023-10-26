package com.niveon.mealrecipe.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.niveon.mealrecipe.R
import com.niveon.mealrecipe.adapter.AreasAdapter
import com.niveon.mealrecipe.adapter.CategoriesAdapter
import com.niveon.mealrecipe.adapter.FoodsAdapter
import com.niveon.mealrecipe.databinding.FragmentHomeBinding
import com.niveon.mealrecipe.util.*
import com.niveon.mealrecipe.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var categoriesAdapter: CategoriesAdapter

    @Inject
    lateinit var foodsAdapter: FoodsAdapter

    @Inject
    lateinit var connection: CheckConnection

    @Inject
    lateinit var areasAdapter: AreasAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    enum class PageState { EMPTY, NETWORK, NONE }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleScope.launch {

                viewModel.loadFilterList()
                viewModel.filtersListData.observe(viewLifecycleOwner) {
                    filterSpinner.setupListWithAdapter(it) { letter ->
                        viewModel.getFoodsList(letter)
                    }
                }

                viewModel.getCategoriesList()
                viewModel.categoriesList.observe(viewLifecycleOwner) {
                    when (it.status) {
                        DataStatus.Status.LOADING -> {
                            homeCategoryLoading.isVisible(true, categoryList)
                        }
                        DataStatus.Status.SUCCESS -> {
                            homeCategoryLoading.isVisible(false, categoryList)
                            categoriesAdapter.setData(it.data!!.categories)
                            categoryList.setupRecyclerView(
                                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false),
                                categoriesAdapter
                            )
                        }
                        DataStatus.Status.ERROR -> {
                            homeCategoryLoading.isVisible(false, categoryList)
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                categoriesAdapter.setOnItemClickListener {
                    categoriesAdapter.selectedItem = categoriesAdapter.moviesList.indexOf(it)
                    categoriesAdapter.notifyDataSetChanged()
                    areasAdapter.resetSelection()
                    viewModel.getFoodByCategory(it.strCategory.toString())
                }

                viewModel.getAreasList()
                viewModel.areaList.observe(viewLifecycleOwner){
                    when (it.status) {
                        DataStatus.Status.LOADING -> {
                            homeAreaLoading.isVisible(true, areaList)
                        }
                        DataStatus.Status.SUCCESS -> {
                            homeAreaLoading.isVisible(false, areaList)
                            areasAdapter.setData(it.data!!.areas)
                            areaList.setupRecyclerView(
                                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false),
                                areasAdapter
                            )
                        }
                        DataStatus.Status.ERROR -> {
                            homeAreaLoading.isVisible(false, areaList)
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                areasAdapter.setOnItemClickListener {
                    areasAdapter.selectedItem = areasAdapter.moviesList.indexOf(it)
                    areasAdapter.notifyDataSetChanged()
                    categoriesAdapter.resetSelection()
                    viewModel.getFoodByArea(it.strArea.toString())
                }

                viewModel.getFoodsList("A")
                viewModel.foodList.observe(viewLifecycleOwner) {
                    when (it.status) {
                        DataStatus.Status.LOADING -> {
                            homeFoodsLoading.isVisible(true, foodsList)
                        }
                        DataStatus.Status.SUCCESS -> {
                            homeFoodsLoading.isVisible(false, foodsList)
                            if (it.data!!.meals != null) {
                                if (it.data.meals!!.isNotEmpty()) {
                                    checkConnectionOrEmpty(false, PageState.NONE)
                                    foodsAdapter.setData(it.data.meals)
                                    foodsList.setupRecyclerView(
                                        LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false),
                                        foodsAdapter
                                    )
                                }
                            } else {
                                checkConnectionOrEmpty(true, PageState.EMPTY)
                            }
                        }
                        DataStatus.Status.ERROR -> {
                            homeFoodsLoading.isVisible(false, foodsList)
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                foodsAdapter.setOnItemClickListener {
                    val direction = HomeFragmentDirections.actionHomeToDetail(it.idMeal!!.toInt())
                    findNavController().navigate(direction)
                }

                searchEdt.addTextChangedListener {
                    if (it.toString().length > 2) {
                        viewModel.getFoodBySearch(it.toString())
                    }
                }

                connection.observe(viewLifecycleOwner) {
                    if (it) {
                        checkConnectionOrEmpty(false, PageState.NONE)
                    } else {
                        checkConnectionOrEmpty(true, PageState.NETWORK)
                    }
                }

                imgOpenGithub.setOnClickListener {
                    val uri = Uri.parse("https://github.com/aryafebian07")
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, uri), "Open with"))
                }
            }
        }
    }

    private fun checkConnectionOrEmpty(isShownError: Boolean, state: PageState) {
        binding?.apply {
            if (isShownError) {
                homeDisLay.isVisible(true, homeContent)
                when (state) {
                    PageState.EMPTY -> {
                        homeContent.visibility = View.GONE
                        homeDisLay.visibility = View.VISIBLE
                        disconnectLay.imgDisconnect.setAnimation(R.raw.empty)
                        disconnectLay.imgDisconnect.playAnimation()
                    }
                    PageState.NETWORK -> {
                        homeContent.visibility = View.GONE
                        homeDisLay.visibility = View.VISIBLE
                        disconnectLay.imgDisconnect.setAnimation(R.raw.nointernet)
                        disconnectLay.imgDisconnect.playAnimation()
                    }
                    else -> {}
                }
            } else {
                homeDisLay.isVisible(false, homeContent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}