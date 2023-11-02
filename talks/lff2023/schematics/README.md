# Schematics

Schematics have been created with the Horizon EDA software,
version 2.5.0 Kepler.  Project site of Horizon EDA:

https://horizon-eda.org/

For correctly rendering the parts that are used in the schematics,
make sure that the Horizon default pool has been added.

At the moment, there does not seem to be any way to export schematic
sheets programmatically.  Consequently, all sheets of the project
have been manually exported via Horizon EDA's GUI and stored in this
repository directory as PDF file <code>tp-sim.pdf</code>.  During
the process of building the slides, this PDF file is split into
all contained sheets, and each sheet is automatically coverted to EPS
with tight bounding box for inclusion in the LaTeX Beamer slides.
