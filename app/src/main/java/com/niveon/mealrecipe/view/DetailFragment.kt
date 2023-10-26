package com.niveon.mealrecipe.view

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.gson.Gson
import com.niveon.mealrecipe.R
import com.niveon.mealrecipe.databinding.FragmentDetailBinding
import com.niveon.mealrecipe.db.FoodEntity
import com.niveon.mealrecipe.util.CheckConnection
import com.niveon.mealrecipe.util.Constants.VIDEO_ID
import com.niveon.mealrecipe.util.DataStatus
import com.niveon.mealrecipe.util.isVisible
import com.niveon.mealrecipe.viewmodel.DetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import javax.inject.Inject

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var connection: CheckConnection

    @Inject
    lateinit var entity: FoodEntity


    private val args: DetailFragmentArgs by navArgs()
    private var foodID = 0
    private val viewModel: DetailViewModel by viewModels()
    private var isFavorite = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            foodID = args.foodId

            detailBack.setOnClickListener { findNavController().navigateUp() }

            viewModel.loadFoodDetail(foodID)
            viewModel.foodDetailData.observe(viewLifecycleOwner) {
                when (it.status) {
                    DataStatus.Status.LOADING -> {
                        detailLoading.isVisible(true, detailContentLay)
                    }
                    DataStatus.Status.SUCCESS -> {
                        detailLoading.isVisible(false, detailContentLay)

                        it.data?.meals?.get(0)?.let { itMeal ->

                            entity.id = itMeal.idMeal!!.toInt()
                            entity.title = itMeal.strMeal.toString()
                            entity.img = itMeal.strMealThumb.toString()

                            foodCoverImg.load(itMeal.strMealThumb) {
                                crossfade(true)
                                crossfade(500)
                            }
                            foodCategoryTxt.text = itMeal.strCategory
                            foodAreaTxt.text = itMeal.strArea
                            foodTitleTxt.text = itMeal.strMeal
                            foodDescTxt.text = itMeal.strInstructions

                            if (itMeal.strYoutube != null) {
                                foodPlayCv.visibility = View.VISIBLE
                                foodPlayCv.setOnClickListener {
                                    val videoId = itMeal.strYoutube.split("=")[1]
                                    val urlYoutube = "https://www.youtube.com/watch?v=$videoId"
                                    Log.d("Ryax",urlYoutube)
                                    Intent(Intent.ACTION_VIEW).also {
                                        it.data =  Uri.parse(urlYoutube)
                                        startActivity(it)
                                    }
                                }
                            } else {
                                foodPlayCv.visibility = View.GONE
                            }

                            if (itMeal.strSource != null) {
                                foodSourceCv.visibility = View.VISIBLE
                                foodSharedCv.visibility = View.VISIBLE

                                foodSourceCv.setOnClickListener {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(itMeal.strSource)))
                                }
                                foodSharedCv.setOnClickListener {
                                    val shareIntent = Intent(Intent.ACTION_SEND)
                                    shareIntent.type="text/plain"
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, itMeal.strSource)
                                    startActivity(Intent.createChooser(shareIntent, "Share with"))
                                }
                            } else {
                                foodSourceCv.visibility = View.GONE
                                foodSharedCv.visibility = View.GONE
                            }
                        }

                        val jsonData = JSONObject(Gson().toJson(it.data))
                        val meals = jsonData.getJSONArray("meals")
                        val meal = meals.getJSONObject(0)

                        for (i in 1..15) {
                            val ingredient = meal.getString("strIngredient$i")
                            if (ingredient.isNullOrEmpty().not()) {
                                ingredientsTxt.append("$ingredient\n")
                            }
                        }

                        for (i in 1..15) {
                            val measure = meal.getString("strMeasure$i")
                            if (measure.isNullOrEmpty().not()) {
                                measureTxt.append("$measure\n")
                            }
                        }
                    }
                    DataStatus.Status.ERROR -> {
                        detailLoading.isVisible(false, detailContentLay)
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            viewModel.existsFood(foodID)
            viewModel.isSavedData.observe(viewLifecycleOwner) {
                isFavorite = it
                if (it) {
                    detailSaved.setColorFilter(ContextCompat.getColor(requireContext(), R.color.tartOrange))
                } else {
                    detailSaved.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
            detailSaved.setOnClickListener {
                if (isFavorite) {
                    viewModel.deleteFood(entity)
                } else
                    viewModel.saveFood(entity)
            }
        }

        connection.observe(viewLifecycleOwner) {
            if (it) {
                checkConnectionOrEmpty(false, HomeFragment.PageState.NONE)
            } else {
                checkConnectionOrEmpty(true, HomeFragment.PageState.NETWORK)
            }
        }
    }


    private fun checkConnectionOrEmpty(isShownError: Boolean, state: HomeFragment.PageState) {
        binding?.apply {
            if (isShownError) {
                homeDisLay.isVisible(true, detailContentLay)
                when (state) {
                    HomeFragment.PageState.EMPTY -> {
                        detailContentLay.visibility = View.GONE
                        homeDisLay.visibility = View.VISIBLE
                        disconnectLay.imgDisconnect.setAnimation(R.raw.empty)
                        disconnectLay.imgDisconnect.playAnimation()
                    }
                    HomeFragment.PageState.NETWORK -> {
                        detailContentLay.visibility = View.GONE
                        homeDisLay.visibility = View.VISIBLE
                        disconnectLay.imgDisconnect.setAnimation(R.raw.nointernet)
                        disconnectLay.imgDisconnect.playAnimation()
                    }
                    else -> {}
                }
            } else {
                homeDisLay.isVisible(false, detailContentLay)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}