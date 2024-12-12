$(document).ready(function() {
    /*
    RHYME FINDER JS SECTION
    */

    removeAllRemovableBrTags = function() {
        let removable_br_tags = Array.from($(".removable_br"));

        for (let i = 0; i < removable_br_tags.length; i++) {
            removable_br_tags[i].parentElement.removeChild(removable_br_tags[i]);
        }
    }

    removeAllRhymeContainers = function() {
        let containers = Array.from($(".rhyme_letter_container"));

        for (let i = 0; i < containers.length; i++) {
            containers[i].parentElement.removeChild(containers[i]);
        }

        removeAllRemovableBrTags();
    }

    getRhymingWordsAndPopulate = function(language, word) {
        $.get(window.location.pathname + 'GetRhymingWords', {
                "language": language,
                "word": word
            },
            function(resp) {
                console.log(resp);
                rhyming_words = resp.rhymingWords;
                let keys = Object.keys(rhyming_words);
                keys.sort();
                for (let i = keys.length - 1; i >= 0; i--) {
                    let key = keys[i];
                    populateRhymeSection(key, rhyming_words[key])
                }

                $(".rhyming_word").click(function() {
                    selectText(this);
                });

                $(".rhyming_word").dblclick(function() {
                    let word = $(this).text();
                    $("[tab_id='phrase_finder_tab']").click();
                    $("#phrase_finder_word").val(word);
                    $("#findphrases").click();
                });


            });
    }

    populateRhymeSection = function(rhyme_by_last_n_letters, rhyming_words_array) {
        if (rhyming_words_array.length == 0) {
            return;
        }

        let rhyme_section = getRhymeSectionTemplateElement(rhyme_by_last_n_letters, rhyming_words_array);

        $("#rhyming_words_category_container").append(rhyme_section);
    }

    getRhymeSectionTemplateElement = function(rhyme_by_last_n_letters, rhyming_words_array) {
        let template = document.getElementById("rhyme_letter_container_template");
        let clone = template.content.cloneNode(true);

        let header_container = $(clone).find(".rhyme_section_header");
        let header_text = header_container.text().replaceAll("$n$", rhyme_by_last_n_letters);
        header_container.text(header_text);

        for (let i = 0; i < rhyming_words_array.length; i++) {
            let words_container = $(clone).find(".rhyming_words_container");
            let word = rhyming_words_array[i];
            words_container.append(
                "<span class='rhyming_word' title='double click to find phrases with this word'>" + word + "</span]>"
            )

            if ((i + 1) % 5 == 0) {
                words_container.append("<br class='removable_br'>");
            }
        }

        return clone;
    }

    selectText = function(element) {
        if (window.getSelection) {
            var range = document.createRange();
            range.selectNode(element);
            window.getSelection().removeAllRanges();
            window.getSelection().addRange(range);
        }
    }

    //event handlers
    $("#findrhymingwords").click(function() {
        //resetting UI on retry
        removeAllRhymeContainers();
        //calling API
        getRhymingWordsAndPopulate("tamil", $("#rhyme_finder_word").val());
    });

    $(".tablinks").click(function() {
        let current_tab_link = this;
        $('.tablinks').each(function(i, tablink) {
            if (current_tab_link == tablink) {
                $("#" + this.getAttribute("tab_id") + "").show();
                tablink.classList.add("active");
            } else {
                $("#" + this.getAttribute("tab_id") + "").hide();
                tablink.classList.remove("active");
            }
        });
    });

    /*
    PHRASE FINDER JS SECTION
    */

    //Find Button Handling
    removeAllPhraseContainers = function() {
        let containers = Array.from($(".phrase"));

        for (let i = 0; i < containers.length; i++) {
            containers[i].parentElement.removeChild(containers[i]);
        }

        removeAllRemovableBrTags();
    }

    getPhrasesAndPopulate = function(language, word) {
        $.ajax({
            url: window.location.pathname + 'GetPhrasesWithWord',
            type: 'GET',
            data: {
                "language": language,
                "word": word
            },
            success: function(resp) {
                phrases = resp.phrases;

                populatePhrases(phrases)

                $(".phrase").click(function() {
                    selectText(this);
                });
            },
            error: function(error) {
                alert(error.responseJSON.error_message);
            }
        });
    }

    populatePhrases = function(phrases) {
        let phrases_container = $("#phrases_container");

        for (let i = 0; i < phrases.length; i++) {
            phrases_container.append(
                "<span class='phrase'>" + phrases[i] + "</span]>"
            )

            if ((i + 1) % 2 == 0) {
                phrases_container.append("<br class='removable_br'>");
            }
        }

    }

    //event handlers
    $("#findphrases").click(function() {
        //resetting UI on retry
        removeAllPhraseContainers();
        //calling API
        getPhrasesAndPopulate("tamil", $("#phrase_finder_word").val());
    });

    $(".tablinks").click(function() {
        let current_tab_link = this;
        $('.tablinks').each(function(i, tablink) {
            if (current_tab_link == tablink) {
                $("#" + this.getAttribute("tab_id") + "").show();
                tablink.classList.add("active");
            } else {
                $("#" + this.getAttribute("tab_id") + "").hide();
                tablink.classList.remove("active");
            }
        });
    });

    /*
    PHONETICALLY SIMILAR WORDS JS SECTION
    */

    removeAllPhoneticallySimilarWordsContainers = function() {
        let containers = Array.from($(".phonetically_similar_word"));

        for (let i = 0; i < containers.length; i++) {
            containers[i].parentElement.removeChild(containers[i]);
        }

        removeAllRemovableBrTags();
    }

    getPhoneticallySimilarWordsAndPopulate = function(language, word, params) {
        $.get(window.location.pathname + 'GetSimilarWords', {
                "language": language,
                "word": word,
                "isMatchingConsonantVowelPattern": params.isMatchingConsonantVowelPattern,
                "isMatchingSoundexCode": params.isMatchingSoundexCode,
                "isMatchingMetaphoneCode": params.isMatchingMetaphoneCode,
                "isMatchingSyllableCount": params.isMatchingSyllableCount,
                "isMatchingStressPattern": params.isMatchingStressPattern
            },
            function(resp) {
                let similarWords = resp.similarWords;
                // Directly populate the words without sorting them into sections
                populatePhoneticallySimilarWords(similarWords);

                $(".phonetically_similar_word").click(function() {
                    selectText(this);
                });

                $(".phonetically_similar_word").dblclick(function() {
                    let word = $(this).text();
                    $("[tab_id='phrase_finder_tab']").click();
                    $("#phrase_finder_word").val(word);
                    $("#findphrases").click();
                });
            });
    }

    populatePhoneticallySimilarWords = function(similarWordsArray) {
        if (similarWordsArray.length == 0) {
            return;
        }

        // Loop through all the similar words and append them directly
        for (let i = 0; i < similarWordsArray.length; i++) {
            let word = similarWordsArray[i];

			$("#phonetically_similar_words_container").append(
                "<span class='phonetically_similar_word' title='double click to find phrases with this word'>" + word + "</span>"
            );

            // Optionally add a line break after every 5 words for readability
            if ((i + 1) % 5 == 0) {
                $("#phonetically_similar_words_container").append("<br class='removable_br'>");
            }
        }
    }

    // Store the toggle options in local storage
    storeToggleOptions = function() {
        let toggleOptions = {
            isMatchingConsonantVowelPattern: $("#isMatchingConsonantVowelPattern").prop("checked"),
            isMatchingSoundexCode: $("#isMatchingSoundexCode").prop("checked"),
            isMatchingMetaphoneCode: $("#isMatchingMetaphoneCode").prop("checked"),
            isMatchingSyllableCount: $("#isMatchingSyllableCount").prop("checked"),
            isMatchingStressPattern: $("#isMatchingStressPattern").prop("checked")
        };

        localStorage.setItem('toggleOptions', JSON.stringify(toggleOptions));
    }

    // Load the toggle options from local storage
    loadToggleOptions = function() {
        let savedOptions = localStorage.getItem('toggleOptions');
        if (savedOptions) {
            let options = JSON.parse(savedOptions);
            $("#isMatchingConsonantVowelPattern").prop("checked", options.isMatchingConsonantVowelPattern);
            $("#isMatchingSoundexCode").prop("checked", options.isMatchingSoundexCode);
            $("#isMatchingMetaphoneCode").prop("checked", options.isMatchingMetaphoneCode);
            $("#isMatchingSyllableCount").prop("checked", options.isMatchingSyllableCount);
            $("#isMatchingStressPattern").prop("checked", options.isMatchingStressPattern);
        }
    }

    // Event handler for finding phonetically similar words
    $("#findphoneticallysimilarwords").click(function() {
        // Resetting UI on retry
        removeAllPhoneticallySimilarWordsContainers();

        // Get the values from the input and toggles
        let word = $("#phonetically_similar_word").val();

        if (!word) {
            alert("Please enter a word.");
            return;
        }

        let params = {
            isMatchingConsonantVowelPattern: $("#isMatchingConsonantVowelPattern").prop("checked"),
            isMatchingSoundexCode: $("#isMatchingSoundexCode").prop("checked"),
            isMatchingMetaphoneCode: $("#isMatchingMetaphoneCode").prop("checked"),
            isMatchingSyllableCount: $("#isMatchingSyllableCount").prop("checked"),
            isMatchingStressPattern: $("#isMatchingStressPattern").prop("checked")
        };

        // Store the toggle options before making the API call
        storeToggleOptions();

        // Calling the API to get phonetically similar words
        getPhoneticallySimilarWordsAndPopulate("tamil", word, params);
    });

    // Load the toggle options when the page is ready
    loadToggleOptions();

    //code to run after page has loaded	
    handlePageLoad = function() {
        $("[tab_id='rhyme_finder_tab']").click();
    }

    handlePageLoad();
})