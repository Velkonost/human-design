package com.myhumandesignhd.event

import com.myhumandesignhd.model.AboutItem
import com.myhumandesignhd.model.Center
import com.myhumandesignhd.model.TransitionChannel
import com.myhumandesignhd.model.TransitionGate

data class OpenAboutItemEvent(val item: AboutItem)

data class OpenGateItemEvent(val gate: TransitionGate)

data class OpenChannelItemEvent(val channel: TransitionChannel)

data class OpenCenterItemEvent(val center: Center)