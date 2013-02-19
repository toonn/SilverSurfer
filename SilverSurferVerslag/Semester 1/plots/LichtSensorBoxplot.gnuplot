# Plot 8 boxplots, one for each light sensor test situation
set terminal png
set output 'boxplots.png'

set xrange [-2:32]
set yrange [46:58]

set bars 15.0
set style fill empty
set xtics scale 0 rotate by 70 offset -1, -3
set ytics nomirror
unset x2tics; unset y2tics;
set border 2
set bmargin 5

plot 'LichtSensorCalibratie.dat' using 1:3:2:6:5:xticlabels(7) with \
	candlesticks lt 3 lw 2 title 'Lightsensordata' whiskerbars 0.5, \
	'' using 1:4:4:4:4 with candlesticks lt -1 lw 2 notitle
