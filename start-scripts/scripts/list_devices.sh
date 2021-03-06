#!/bin/bash

for sysdevpath in $(find /sys/bus/usb/devices/usb*/ -name dev); do
	(
	syspath="${sysdevpath%/dev}"
	devname="$(udevadm info -q name -p $syspath)"
	[[ "$devname" == "bus/"* ]] && continue
	eval "$(udevadm info -q property --export -p $syspath)"
	echo "/dev/$devname - $ID_VENDOR_ID - $ID_SERIAL_SHORT - $ID_MODEL_ID"
	)
done
