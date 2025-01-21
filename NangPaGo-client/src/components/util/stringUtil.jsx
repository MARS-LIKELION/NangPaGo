import parse from 'html-react-parser';

function parseHighlightedName(htmlString) {
  return parse(htmlString, {
    replace: (domNode) => {
      if (domNode.name === 'em') {
        return (
          <strong className="text-primary">{domNode.children[0]?.data}</strong>
        );
      }
    },
  });
}

function stripHtmlTags(htmlString) {
  return htmlString.replace(/<[^>]+>/g, '');
}

export { parseHighlightedName, stripHtmlTags };
