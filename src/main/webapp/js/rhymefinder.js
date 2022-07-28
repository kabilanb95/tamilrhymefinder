$(document).ready(function()
{	
	//Find Button Handling
	removeAllRhymeContainers=function()
	{
		let containers=Array.from($(".rhyme_letter_container"));
		
		for(let i=0;i<containers.length;i++)
		{
			containers[i].parentElement.removeChild(containers[i]);
		}
	}
	
	getRhymingWordsAndPopulate=function(language,word)
	{
		$.get(window.location.pathname+'GetRhymingWords',
	    {
	        "language":language,
	        "word":word
	    },
	    function(resp)
	    {
			rhyming_words=resp.rhyming_words;
			let keys = Object.keys(rhyming_words);
			keys.sort();
			for(let i=keys.length-1;i>=0;i--)
			{
				let key=keys[i];
				populateRhymeSection(key,rhyming_words[key])
			}
			
			$(".rhyming_word").click(function()
			{
				selectText(this);
			});

		});			
	}
	
	populateRhymeSection=function(rhyme_by_last_n_letters,rhyming_words_array)
	{
		if(rhyming_words_array.length==0)
		{
			return;
		}
		
		let rhyme_section=getRhymeSectionTemplateElement(rhyme_by_last_n_letters,rhyming_words_array);
		
		$("#rhyming_words_category_container").append(rhyme_section);		
	}
	
	getRhymeSectionTemplateElement=function(rhyme_by_last_n_letters,rhyming_words_array)
	{		
		let template = document.getElementById("rhyme_letter_container_template");
		let clone = template.content.cloneNode(true);
		
		let header_container=$(clone).find(".rhyme_section_header");
		let header_text=header_container.text().replaceAll("$n$",rhyme_by_last_n_letters);
		header_container.text(header_text);
		
		for(let i=0;i<rhyming_words_array.length;i++)
		{
			let words_container=$(clone).find(".rhyming_words_container");
			let word=rhyming_words_array[i];
			words_container.append(
				"<span class='rhyming_word'>"+word+"</span]>"		
			)
			
			if((i+1)%5==0)
			{
				words_container.append("<br>");
			}
		}
						
		return clone;
	}
	
	selectText=function(element) 
	{
		if(window.getSelection) 
		{
	        var range = document.createRange();
	        range.selectNode(element);
	        window.getSelection().removeAllRanges();
	        window.getSelection().addRange(range);
    	}
	}
	
	//event handlers
	$("#findrhymingwords").click(function()
	{
		//resetting UI on retry
		removeAllRhymeContainers();
		//calling API
		getRhymingWordsAndPopulate("tamil",$("#rhyme_finder_word").val());
	});
})

