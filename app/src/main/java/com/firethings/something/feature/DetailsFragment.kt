package com.firethings.something.feature

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.firethings.something.R
import com.firethings.something.common.Formatters
import com.firethings.something.common.setThrottlingOnClickListener
import com.firethings.something.core.BaseFragment
import com.firethings.something.databinding.FragmentDetailsBinding
import com.firethings.something.feature.adapter.PropertyListItem
import com.firethings.something.feature.adapter.TitleListItem
import com.firethings.something.presentation.DetailsViewModel
import com.firethings.something.presentation.DetailsViewModel.Action
import com.firethings.something.presentation.DetailsViewModel.Event
import com.firethings.something.presentation.DetailsViewModel.State
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : BaseFragment<Event, Action, State>(R.layout.fragment_details) {
    override val viewModel: DetailsViewModel by viewModel()
    private var binding: FragmentDetailsBinding? = null
    private val args: DetailsFragmentArgs by navArgs()

    private val itemAdapter: ItemAdapter<GenericItem> = ItemAdapter()
    private val adapter: FastAdapter<GenericItem> = FastAdapter.Companion.with(itemAdapter)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailsBinding.bind(view)
        binding?.recycler?.adapter = adapter

        viewModel.sendEvent(Event.LoadWeatherData(args.weatherId))

        binding?.backBtn?.setThrottlingOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun bindState(state: State) {
        binding?.loader?.isVisible = state.isLoading

        // TODO: Refactor and add units
        val contents = state.weather?.let { data ->
            val (weather, _) = data
            listOf(
                TitleListItem(getString(R.string.coord)),
                PropertyListItem(getString(R.string.lat), weather.coord.lat),
                PropertyListItem(getString(R.string.lon), weather.coord.lon),
                TitleListItem(getString(R.string.base), weather.base),
                TitleListItem(getString(R.string.main)),
                PropertyListItem(getString(R.string.temp), weather.main?.temp?.formatted),
                PropertyListItem(getString(R.string.feelsLike), weather.main?.feelsLike?.formatted),
                PropertyListItem(getString(R.string.temp_min), weather.main?.tempMin?.formatted),
                PropertyListItem(getString(R.string.temp_max), weather.main?.tempMax?.formatted),
                PropertyListItem(getString(R.string.pressure), weather.main?.pressure),
                PropertyListItem(getString(R.string.humidity), weather.main?.humidity),
                TitleListItem(getString(R.string.visibility), weather.visibility),
                TitleListItem(getString(R.string.wind)),
                PropertyListItem(getString(R.string.speed), weather.wind?.speed),
                PropertyListItem(getString(R.string.deg), weather.wind?.deg),
                PropertyListItem(getString(R.string.gust), weather.wind?.gust),
                TitleListItem(getString(R.string.vloud)),
                PropertyListItem(getString(R.string.all), weather.clouds?.all),
                TitleListItem(getString(R.string.rain)),
                PropertyListItem(getString(R.string.one_hour), weather.rain?.oneHour),
                PropertyListItem(getString(R.string.three_hour), weather.rain?.threeHour),
                TitleListItem(getString(R.string.snow)),
                PropertyListItem(getString(R.string.one_hour), weather.snow?.oneHour),
                PropertyListItem(getString(R.string.three_hour), weather.snow?.threeHour),
                TitleListItem(getString(R.string.dt), weather.dt),
                TitleListItem(getString(R.string.sys)),
                PropertyListItem(getString(R.string.type), weather.sys?.type),
                PropertyListItem(getString(R.string.sysId), weather.sys?.id),
                PropertyListItem(getString(R.string.message), weather.sys?.message),
                PropertyListItem(getString(R.string.county), weather.sys?.country),
                PropertyListItem(getString(R.string.sunrise),
                    weather.sys?.sunrise?.let { Formatters.dateTimeFormat.format(it) }),
                PropertyListItem(getString(R.string.sunset),
                    weather.sys?.sunset?.let { Formatters.dateTimeFormat.format(it) }),
                TitleListItem(getString(R.string.timezone), weather.timezone),
                TitleListItem(getString(R.string.cityId), weather.cityId),
                TitleListItem(getString(R.string.name), weather.name),
                TitleListItem(getString(R.string.code), weather.cod),
                TitleListItem(getString(R.string.unit), weather.parameterUnit.name),
                TitleListItem(getString(R.string.date), Formatters.dateTimeFormat.format(weather.date)),
            )
        } ?: emptyList<GenericItem>()

        val contentResult = FastAdapterDiffUtil.calculateDiff(itemAdapter, contents)
        FastAdapterDiffUtil[itemAdapter] = contentResult

        if (state.error != null) {
            Toast.makeText(
                context,
                String.format(getString(R.string.error_value), state.error.localizedMessage), Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroyView() {
        binding?.recycler?.adapter = null
        binding = null
        super.onDestroyView()
    }
}