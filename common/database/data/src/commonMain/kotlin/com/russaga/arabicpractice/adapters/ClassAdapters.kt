package com.russaga.arabicpractice.adapters

import migrations.CityLocation
import migrations.CityName

internal val cityLocationAdapter = CityLocation.Adapter(
    cityIdAdapter = LongColumnAdapter,
    longitudeAdapter = FloatColumnAdapter,
    latitudeAdapter = FloatColumnAdapter
)
internal val cityNameAdapter = CityName.Adapter(
    idAdapter = LongColumnAdapter,
    languageIdAdapter = LongColumnAdapter
)