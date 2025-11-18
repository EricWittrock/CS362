public class Option {
	private String name;
	private OptionSelect onSelect;

	// Construct like new Option("Option Name", () -> { /* code to execute */ });
	public Option(String name, OptionSelect onSelect) {
		this.name = name;
		this.onSelect = onSelect;
	}
	
	public void select() {
		this.onSelect.select();
	}
	
	public void Display(int index) {
		System.out.println(index + ") " + name);
	}
}