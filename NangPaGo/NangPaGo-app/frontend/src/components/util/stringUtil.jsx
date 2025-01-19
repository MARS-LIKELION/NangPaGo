import parse from 'html-react-parser';

export const parseHighlightedName = (htmlString) => {
  return parse(htmlString, {
    replace: (domNode) => {
      if (domNode.name === 'em') {
        return (
          <strong style={{ color: 'var(--secondary-color)' }}>
            {domNode.children[0]?.data}
          </strong>
        );
      }
    },
  });
};

export const stripHtmlTags = (htmlString) => {
  return htmlString.replace(/<[^>]+>/g, '');
};
