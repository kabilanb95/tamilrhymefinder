$(document).ready(function()
{
	/*
	RHYME FINDER JS SECTION
	*/
	
	removeAllRemovableBrTags=function()
	{
		let removable_br_tags=Array.from($(".removable_br"));
		
		for(let i=0;i<removable_br_tags.length;i++)
		{
			removable_br_tags[i].parentElement.removeChild(removable_br_tags[i]);
		}		
	}
	
	removeAllRhymeContainers=function()
	{		
		let containers=Array.from($(".rhyme_letter_container"));
		
		for(let i=0;i<containers.length;i++)
		{
			containers[i].parentElement.removeChild(containers[i]);
		}
		
		removeAllRemovableBrTags();
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
			
			$(".rhyming_word").dblclick(function()
			{
				let word=$(this).text();
				$("[tab_id='phrase_finder_tab']").click();
				$("#phrase_finder_word").val(word);
				$("#findphrases").click();
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
				words_container.append("<br class='removable_br'>");
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
	
	$(".tablinks").click(function()
	{
		let current_tab_link=this;
		$('.tablinks').each(function(i, tablink) 
		{			
	    	if(current_tab_link==tablink)
	    	{
				$("#"+this.getAttribute("tab_id")+"").show();
				tablink.classList.add("active");
			}
			else
			{
				$("#"+this.getAttribute("tab_id")+"").hide();
				tablink.classList.remove("active");
			}
	    });
	});	

	/*
	PHRASE FINDER JS SECTION
	*/
	
	//Find Button Handling
	removeAllPhraseContainers=function()
	{
		let containers=Array.from($(".phrase"));
		
		for(let i=0;i<containers.length;i++)
		{
			containers[i].parentElement.removeChild(containers[i]);
		}
		
		removeAllRemovableBrTags();
	}
	
	getPhrasesAndPopulate=function(language,word)
	{
		$.ajax(
		{
		    url: window.location.pathname+'GetPhrasesWithWord',
		    type: 'GET',
		    data: {"language":language,"word":word},
		    success: function(resp)
		    { 
				phrases=resp.phrases;
	
				populatePhrases(phrases)
				
				$(".phrase").click(function()
				{
					selectText(this);
				});
		    },
		    error: function(error) 
		    {
				alert(error.responseJSON.error_message);
		    }
		});	
	}
	
	populatePhrases=function(phrases)
	{
		let phrases_container=$("#phrases_container");

		for(let i=0;i<phrases.length;i++)
		{
			phrases_container.append(
				"<span class='phrase'>"+phrases[i]+"</span]>"		
			)	
			
			if((i+1)%2==0)
			{
				phrases_container.append("<br class='removable_br'>");
			}
		}
		
	}
		
	//event handlers
	$("#findphrases").click(function()
	{
		//resetting UI on retry
		removeAllPhraseContainers();
		//calling API
		getPhrasesAndPopulate("tamil",$("#phrase_finder_word").val());
	});
	
	$(".tablinks").click(function()
	{
		let current_tab_link=this;
		$('.tablinks').each(function(i, tablink) 
		{			
	    	if(current_tab_link==tablink)
	    	{
				$("#"+this.getAttribute("tab_id")+"").show();
				tablink.classList.add("active");
			}
			else
			{
				$("#"+this.getAttribute("tab_id")+"").hide();
				tablink.classList.remove("active");
			}
	    });
	});
	
	//code to run after page has loaded	
	handlePageLoad=function()
	{
		$("[tab_id='rhyme_finder_tab']").click();
	}
					
	handlePageLoad();
})


