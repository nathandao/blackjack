require 'libusb'

`./scripts/init.sh`

usb = LIBUSB::Context.new

usb.devices.each() do |d|
  puts "serial: #{d.serial_number}"
  puts "idProduct: #{d.idProduct}"
  puts "idVendor: #{d.idVendor}"
  puts "\n"
end
