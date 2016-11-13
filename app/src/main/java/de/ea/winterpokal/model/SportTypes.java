package de.ea.winterpokal.model;

public enum SportTypes {
	radfahren, laufen, skilanglauf, alternative_sportarten, total;

	SportTypes() {

	}

	public String toString() {
		switch (this) {
		case radfahren:
			return "Radfahren";
		case laufen:
			return "Laufen";
		case skilanglauf:
			return "Skilanglauf";
		case alternative_sportarten:
			return "Alternative Sportarten";
		default:
			return this.name();
		}
	}
}
