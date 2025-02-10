function RecipeImage({ imageRef, mainImage, recipeName }) {
  return (
    <div className="lg:w-7/12 md:w-1/2">
      <div className="rounded-md flex justify-center items-center aspect-square object-cover overflow-hidden bg-center">
        <img className="w-full min-h-full rounded-md"
             ref={imageRef}
             src={mainImage}
             alt={recipeName}
        />
      </div>
    </div>
  );
}

export default RecipeImage;
