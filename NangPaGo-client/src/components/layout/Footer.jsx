import React, { useState } from 'react';
import BuildVersionModal from '../../components/modal/BuildVersionModal';
import versionFetcher from '../../api/footer';
import { FOOTER } from '../../common/constants/links';

function Footer() {
  const [showBuildVersionModal, setShowBuildVersionModal] = useState(false);
  const { version, fetchVersion } = versionFetcher();

  return (
    <footer className="sticky bottom-0 right-0 bg-gray-100 p-4 flex flex-col items-center text-center md:flex-row md:justify-between">
      <div className="flex gap-4 mb-2 md:mb-0 z-10">
        <a
          href={FOOTER.GITHUB_LINK}
          target="_blank"
          rel="noopener noreferrer"
          className="text-secondary text-sm hover:underline transition-colors duration-200"
        >
          팀 소개
        </a>
        <a
          href={FOOTER.FEEDBACK_LINK}
          target="_blank"
          rel="noopener noreferrer"
          className="text-secondary text-sm hover:underline transition-colors duration-200"
        >
          서비스 피드백
        </a>
      </div>
      <p
        className="text-gray-900 text-sm cursor-pointer"
        onClick={() => {
          fetchVersion();
          setShowBuildVersionModal(true);
        }}
      >
        © 2025 NANGPAGO. ALL RIGHTS RESERVED.
      </p>
      {showBuildVersionModal && (
        <BuildVersionModal
          isOpen={showBuildVersionModal}
          onClose={() => setShowBuildVersionModal(false)}
          version={version}
        />
      )}
    </footer>
  );
}

export default Footer;
